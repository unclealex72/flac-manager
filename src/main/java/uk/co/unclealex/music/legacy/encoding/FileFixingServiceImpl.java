package uk.co.unclealex.music.legacy.encoding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

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

import uk.co.unclealex.music.legacy.Constants;
import uk.co.unclealex.music.legacy.FileService;
import uk.co.unclealex.music.legacy.LatinService;
import uk.co.unclealex.music.legacy.inject.FlacDirectory;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * The default implementation of {@link FileFixingService}
 * @author alex
 *
 */
public class FileFixingServiceImpl implements FileFixingService {

	private static final Logger log = LoggerFactory.getLogger(FileFixingServiceImpl.class);

	private final FileService i_fileService;
	private final LatinService i_latinService;
	private final ImageService i_imageService;
	private final OwnerOrPictureFileService i_ownerOrPictureFileService;
	private final File i_flacDirectory;
	
	@Inject
	protected FileFixingServiceImpl(
			FileService fileService, LatinService latinService,
			ImageService imageService, OwnerOrPictureFileService ownerOrPictureFileService,
			@FlacDirectory File flacDirectory) {
		super();
		i_fileService = fileService;
		i_latinService = latinService;
		i_imageService = imageService;
		i_ownerOrPictureFileService = ownerOrPictureFileService;
		i_flacDirectory = flacDirectory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fixVariousArtistsAndFlacFilenames(SortedSet<File> flacFiles) {
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

	/**
	 * Add <code>ALBUMARTIST<code> tags to any files under a directory called <code>Various</code>.
	 * @param flacFiles Candidate files to add the tag to.
	 */
	protected void addAlbumArtistTags(Set<File> flacFiles) {
		Predicate<File> variousFlacFilenamePredicate = new Predicate<File>() {
			@Override
			public boolean apply(File file) {
				File artistDirectory = file.getAbsoluteFile().getParentFile().getParentFile();
				return artistDirectory.getName().toLowerCase().startsWith("various");
			}
		};
		Set<File> variousArtistFlacFiles = Sets.newTreeSet(Iterables.filter(flacFiles, variousFlacFilenamePredicate));
		for (File variousArtistFlacFile : variousArtistFlacFiles) {
			addAlbumArtist(variousArtistFlacFile);
		}
	}

	/**
	 * Set the <code>ALBUMARTIST</code> tag to {@link Constants.VARIOUS_ARTISTS}.
	 * @param variousArtistFlacFile The file to alter.
	 */
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
	
	protected void copyOwnershipAndPictureFiles(File sourceDirectory, File targetDirectory) {
		FileService fileService = getFileService();
		for (File file : sourceDirectory.listFiles(getOwnerOrPictureFileService().asFileFilter())) {
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
		File[] childFiles = directory.listFiles();
		if (Iterables.all(Arrays.asList(childFiles), getOwnerOrPictureFileService().asPredicate())) {
			log.info("Deleting empty directory " + directory.getAbsolutePath());
			for (File childFile : childFiles) {
				childFile.delete();
			}
			directory.delete();
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
					Joiner.on(", ").join(missingFields));
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

	public LatinService getLatinService() {
		return i_latinService;
	}

	public ImageService getImageService() {
		return i_imageService;
	}

	public File getFlacDirectory() {
		return i_flacDirectory;
	}

	public OwnerOrPictureFileService getOwnerOrPictureFileService() {
		return i_ownerOrPictureFileService;
	}
}
