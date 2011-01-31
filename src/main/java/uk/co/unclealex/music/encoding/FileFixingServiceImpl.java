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

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
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
		addAlbumArtistTags(flacFiles);
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

	protected void addAlbumArtistTags(Set<File> flacFiles) {
		Predicate<File> variousFlacFilenamePredicate = new Predicate<File>() {
			@Override
			public boolean evaluate(File file) {
				File artistDirectory = file.getParentFile().getParentFile();
				return artistDirectory.getName().toLowerCase().startsWith("various");
			}
		};
		Set<File> variousArtistFlacFiles = CollectionUtils.select(flacFiles, variousFlacFilenamePredicate, new TreeSet<File>());
		for (File variousArtistFlacFile : variousArtistFlacFiles) {
			addAlbumArtist(variousArtistFlacFile);
		}
	}

	protected void addAlbumArtist(File variousArtistFlacFile) {
		try {
			AudioFile audioFile = AudioFileIO.read(variousArtistFlacFile);
			Tag tag = audioFile.getTag();
			if (tag.getFields(FieldKey.ALBUM_ARTIST).isEmpty()) {
				log.info("Adding various artist tag to " + variousArtistFlacFile);
				tag.setField(FieldKey.ALBUM_ARTIST, Constants.VARIOUS_ARTISTS);
				audioFile.commit();
			}
		}
		catch (CannotReadException e) {
			log.warn("Could not add the various artist tag to " + variousArtistFlacFile, e);
		}
		catch (IOException e) {
			log.warn("Could not add the various artist tag to " + variousArtistFlacFile, e);
		}
		catch (TagException e) {
			log.warn("Could not add the various artist tag to " + variousArtistFlacFile, e);
		}
		catch (ReadOnlyFileException e) {
			log.warn("Could not add the various artist tag to " + variousArtistFlacFile, e);
		}
		catch (InvalidAudioFrameException e) {
			log.warn("Could not add the various artist tag to " + variousArtistFlacFile, e);
		}
		catch (CannotWriteException e) {
			log.warn("Could not add the various artist tag to " + variousArtistFlacFile, e);
		}
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
		String artist = trackInformationBean.getArtist();
		String album = trackInformationBean.getAlbum();
		int trackNumber = trackInformationBean.getTrackNumber();
		String title = trackInformationBean.getTitle();
		boolean compilation = trackInformationBean.isCompilation();
		String artistName = normalise(compilation?Constants.VARIOUS_ARTISTS:artist);
		String albumName = normalise(album);
		String trackName = 
			normalise(String.format("%02d %s %s", trackNumber, compilation?artist:"", title));
		File newFlacFile = 
			new File(new File(new File(baseDirectory, artistName), albumName), trackName + "." + Constants.FLAC);
		return newFlacFile;
	}

	@Override
	public File getFixedFlacFilename(String artist, String album, boolean compilation, int trackNumber, String title) {
		return normaliseFlacFilename(new TrackInformationBean(artist, album, compilation, trackNumber, title), getFlacDirectory());
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
			Tag tag = audioFile.getTag();
			String artist = extractTag(tag, FieldKey.ARTIST, missingFields);
			String album = extractTag(tag, FieldKey.ALBUM, missingFields);
			Integer trackNumber = extractIntegerTag(tag, FieldKey.TRACK, missingFields);
			String title = extractTag(tag, FieldKey.TITLE, missingFields);
			boolean compilation = Constants.VARIOUS_ARTISTS.equals(tag.getFirst(FieldKey.ALBUM_ARTIST));
			if (missingFields.isEmpty()) {
				trackInformationBean = new TrackInformationBean(artist, album, compilation, trackNumber, title);
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
