package uk.co.unclealex.music.encoding;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.music.Constants;
import uk.co.unclealex.music.FileService;
import uk.co.unclealex.music.LatinService;

public class FileFixingServiceImpl implements FileFixingService, FileFilter {

	private static final Logger log = LoggerFactory.getLogger(FileFixingServiceImpl.class);

	private FileService i_fileService;
	private LatinService i_latinService;
	private ImageService i_imageService;
	private File i_flacDirectory;
	
	@Override
	public void fixFlacFilenames(Collection<File> flacFiles) {
		Set<File> flacFileSet;
		if (flacFiles instanceof Set) {
			flacFileSet = (Set<File>) flacFiles;
		}
		else {
			flacFileSet = new HashSet<File>(flacFiles);
		}
		fixFlacFilenames(flacFileSet);
	}

	protected void fixFlacFilenames(Set<File> flacFiles) {
		SortedMap<TrackInformationBean, SortedSet<File>> filesByTrackInformationAndLastModified = 
			sortFilesByTrackInformationAndLastModified(flacFiles);
		SortedMap<TrackInformationBean, File> filesByTrackInformation = purgeOlderFiles(filesByTrackInformationAndLastModified);
		SortedSet<File> alteredAlbumDirectories = new TreeSet<File>();
		SortedSet<File> alteredArtistDirectories = new TreeSet<File>();
		for (Entry<TrackInformationBean, File> entry : filesByTrackInformation.entrySet()) {
			TrackInformationBean trackInformationBean = entry.getKey();
			File flacFile = entry.getValue();
			moveFile(trackInformationBean, flacFile, alteredAlbumDirectories, alteredArtistDirectories);
		}
		removeEmptyDirectories(alteredAlbumDirectories);
		removeEmptyDirectories(alteredArtistDirectories);
	}

	protected void moveFile(TrackInformationBean trackInformationBean, File flacFile,
			SortedSet<File> alteredAlbumDirectories, SortedSet<File> alteredArtistDirectories) {
		File albumDirectory = flacFile.getParentFile();
		File artistDirectory = albumDirectory.getParentFile();
		File baseDirectory = artistDirectory.getParentFile();
		File newFlacFile = normaliseFlacFilename(trackInformationBean, baseDirectory);
		try {
			if (getFileService().move(flacFile, newFlacFile, true)) {
				alteredAlbumDirectories.add(albumDirectory);
				alteredArtistDirectories.add(artistDirectory);
				File newAlbumDirectory = newFlacFile.getParentFile();
				File newArtistDirectory = newAlbumDirectory.getParentFile();
				copyOwnershipAndPictureFiles(albumDirectory, newAlbumDirectory);
				copyOwnershipAndPictureFiles(artistDirectory, newArtistDirectory);
			}
		}
		catch (IOException e) {
			log.error("Cannot move file " + flacFile.getAbsolutePath(), e);
		}
	}

	protected File normaliseFlacFilename(TrackInformationBean trackInformationBean, File baseDirectory) {
		String artistName = normalise(trackInformationBean.getArtist());
		String albumName = normalise(trackInformationBean.getAlbum());
		String trackName = normalise(String.format("%02d %s", trackInformationBean.getTrackNumber(), trackInformationBean.getTitle()));
		File newFlacFile = new File(new File(new File(baseDirectory, artistName), albumName), trackName + "." + Constants.FLAC);
		return newFlacFile;
	}

	@Override
	public File getFixedFlacFilename(String artist, String album, int trackNumber, String title) {
		return normaliseFlacFilename(new TrackInformationBean(artist, album, trackNumber, title), getFlacDirectory());
	}
	
	@Override
	public boolean accept(File file) {
		try {
			return 
				file.isFile() && 
				(Constants.OWNER.equals(FilenameUtils.getBaseName(file.getName())) || getImageService().loadImage(file) != null);
		}
		catch (IOException e) {
			return false;
		}
	}
	
	protected void copyOwnershipAndPictureFiles(File sourceDirectory, File targetDirectory) {
		FileService fileService = getFileService();
		for (File file : sourceDirectory.listFiles(this)) {
			File targetFile = new File(targetDirectory, file.getName());
			try {
				fileService.copy(file, targetFile, true);
			}
			catch (IOException e) {
				log.error("Cannot copy " + file.getAbsolutePath() + " to " + targetFile.getAbsolutePath(), e);
			}
		}
	}

	protected void removeEmptyDirectories(SortedSet<File> directories) {
		for (File directory : directories) {
			removeEmptyDirectory(directory);
		}
	}

	protected void removeEmptyDirectory(File directory) {
		if (directory.list().length == directory.listFiles(this).length) {
			try {
				log.info("Deleting empty directory " + directory.getAbsolutePath());
				FileUtils.deleteDirectory(directory);
			}
			catch (IOException e) {
				log.error("Cannot delete directory " + directory.getAbsolutePath());
			}
		}
	}

	protected String normalise(String text) {
		text = getLatinService().removeCommonAccents(text).toLowerCase();
		text = text.replaceAll("\\s+", "_");
		return text;
	}

	protected SortedMap<TrackInformationBean, SortedSet<File>> sortFilesByTrackInformationAndLastModified(
			Set<File> flacFiles) {
		final Comparator<File> lastModifiedComparator = new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return Long.valueOf(o1.lastModified()).compareTo(Long.valueOf(o2.lastModified()));
			}
		};
		SortedMap<TrackInformationBean, SortedSet<File>> filesByTrackInformationAndLastModified = 
			new TreeMap<TrackInformationBean, SortedSet<File>>();
		for (File flacFile : flacFiles) {
			TrackInformationBean trackInformationBean = extractTrackInformation(flacFile);
			if (trackInformationBean != null) {
				SortedSet<File> filesByLastModified = filesByTrackInformationAndLastModified.get(trackInformationBean);
				if (filesByLastModified == null) {
					filesByLastModified = new TreeSet<File>(lastModifiedComparator);
					filesByTrackInformationAndLastModified.put(trackInformationBean, filesByLastModified);
				}
				filesByLastModified.add(flacFile);
			}
		}
		return filesByTrackInformationAndLastModified;
	}

	protected TrackInformationBean extractTrackInformation(File flacFile) {
		TrackInformationBean trackInformationBean = null;
		try {
			AudioFile audioFile = AudioFileIO.read(flacFile);
			List<String> missingFields = new ArrayList<String>();
			String artist = extractTag(audioFile.getTag(), FieldKey.ARTIST, missingFields);
			String album = extractTag(audioFile.getTag(), FieldKey.ALBUM, missingFields);
			Integer trackNumber = extractIntegerTag(audioFile.getTag(), FieldKey.TRACK, missingFields);
			String title = extractTag(audioFile.getTag(), FieldKey.TITLE, missingFields);
			if (missingFields.isEmpty()) {
				trackInformationBean = new TrackInformationBean(artist, album, trackNumber, title);
			}
			else {
				log.warn(
					"Ignoring file " + flacFile.getAbsolutePath() + " as the following fields are missing: " + 
					StringUtils.join(missingFields, ", "));
			}
		}
		catch (Throwable t) {
			log.error("Cannot read " + flacFile.getAbsolutePath() + ". Is it a valid flac file?", t);
		}
		return trackInformationBean;
	}

	protected String extractTag(Tag tag, FieldKey fieldKey, List<String> missingFields) {
		String tagValue = tag.getFirst(fieldKey);
		if (tagValue == null) {
			missingFields.add(fieldKey.toString());
		}
		return tagValue;
	}

	protected Integer extractIntegerTag(Tag tag, FieldKey fieldKey, List<String> missingFields) {
		Integer intTagValue = null;
		String tagValue = extractTag(tag, fieldKey, missingFields);
		if (tagValue != null) {
			try {
				intTagValue = Integer.valueOf(tagValue);
			}
			catch (NumberFormatException e) {
				missingFields.add(fieldKey.toString());
			}
		}
		return intTagValue;
	}

	protected SortedMap<TrackInformationBean, File> purgeOlderFiles(
			SortedMap<TrackInformationBean, SortedSet<File>> filesByTrackInformationAndLastModified) {
		SortedMap<TrackInformationBean, File> newestFlacFilesByTrackInformationBean = new TreeMap<TrackInformationBean, File>();
		for (Entry<TrackInformationBean, SortedSet<File>> entry : filesByTrackInformationAndLastModified.entrySet()) {
			TrackInformationBean trackInformationBean = entry.getKey();
			SortedSet<File> flacFiles = entry.getValue();
			File newestFlacFile = flacFiles.last();
			String newestFlacPath = newestFlacFile.getAbsolutePath();
			for (File oldFlacFile : flacFiles.headSet(newestFlacFile)) {
				log.info(
						"Removing " + oldFlacFile.getAbsolutePath() + " as it contains the same track as " + 
						newestFlacPath + "(" + trackInformationBean + ")");
				oldFlacFile.delete();
			}
			newestFlacFilesByTrackInformationBean.put(trackInformationBean, newestFlacFile);
		}
		return newestFlacFilesByTrackInformationBean;
	}

	public FileService getFileService() {
		return i_fileService;
	}

	public void setFileService(FileService fileService) {
		i_fileService = fileService;
	}

	public LatinService getLatinService() {
		return i_latinService;
	}

	public void setLatinService(LatinService latinService) {
		i_latinService = latinService;
	}

	public ImageService getImageService() {
		return i_imageService;
	}

	public void setImageService(ImageService imageService) {
		i_imageService = imageService;
	}

	public File getFlacDirectory() {
		return i_flacDirectory;
	}

	public void setFlacDirectory(File flacDirectory) {
		i_flacDirectory = flacDirectory;
	}

}
