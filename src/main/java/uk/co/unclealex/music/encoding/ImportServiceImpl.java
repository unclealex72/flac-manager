package uk.co.unclealex.music.encoding;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.unclealex.music.Constants;
import uk.co.unclealex.music.Encoding;
import uk.co.unclealex.music.FileService;
import uk.co.unclealex.music.inject.Encodings;
import uk.co.unclealex.music.inject.FlacDirectory;

import com.google.common.io.Closeables;
import com.google.inject.Inject;

public class ImportServiceImpl implements ImportService {

	private static final Logger log = LoggerFactory.getLogger(ImportServiceImpl.class);
	
	private SortedSet<Encoding> i_encodings;
	private FileService i_fileService;
	private File i_flacDirectory;

	@Inject
	protected ImportServiceImpl(@Encodings SortedSet<Encoding> encodings, FileService fileService, @FlacDirectory File flacDirectory) {
		super();
		i_encodings = encodings;
		i_fileService = fileService;
		i_flacDirectory = flacDirectory;
	}

	@Override
	public void importFromDirectory(File importDirectory) {
		log.info("Indexing flac files.");
		final Map<TrackInformation, File> flacFiles = indexFlacFiles();
		final Map<String, Encoding> encodingsByExtension = new HashMap<String, Encoding>();
		for (Encoding encoding : getEncodings()) {
			encodingsByExtension.put(encoding.getExtension(), encoding);
		}
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File encodedFile) {
				String extension = FilenameUtils.getExtension(encodedFile.getName());
				Encoding encoding = encodingsByExtension.get(extension);
				if (encoding != null) {
					TrackInformation trackInformation = findTrackInformation(encodedFile);
					if (trackInformation != null) {
						File flacFile = flacFiles.get(trackInformation);
						if (flacFile != null) {
							importFile(flacFile, encodedFile, encoding);
						}
					}
				}
				return false;
			}
		};
		log.info("Searching for importable files.");
		getFileService().listFiles(importDirectory, filter);
		log.info("Done.");
	}

	protected Map<TrackInformation, File> indexFlacFiles() {
		final Map<TrackInformation, File> flacFiles = new HashMap<TrackInformation, File>();
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (Constants.FLAC.equals(FilenameUtils.getExtension(file.getName()))) {
					TrackInformation trackInformation = findTrackInformation(file);
					if (trackInformation != null) {
						flacFiles.put(trackInformation, file);
					}
				}
				return false;
			}
		};
		getFileService().listFiles(getFlacDirectory(), filter);
		return flacFiles;
	}

	protected void importFile(File flacFile, File encodedFile, Encoding encoding) {
		File newEncodedFile = getFileService().translateFlacFileToEncodedFile(flacFile, encoding);
		log.info("Copying" + encodedFile + " to " + newEncodedFile);
		newEncodedFile.getParentFile().mkdirs();
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(encodedFile);
			out = new FileOutputStream(newEncodedFile);
			in.getChannel().transferTo(0, encodedFile.length(), out.getChannel());
			newEncodedFile.setLastModified(encodedFile.lastModified());
		}
		catch (IOException e) {
			log.warn("Copying failed.", e);
		}
		finally {
			Closeables.closeQuietly(in);
			Closeables.closeQuietly(out);
		}
	}

	protected TrackInformation findTrackInformation(File file) {
		TrackInformation trackInformation = null;
		try {
			AudioFile audioFile = AudioFileIO.read(file);
			Tag tag = audioFile.getTag();
			String artist = tag.getFirst(FieldKey.ARTIST);
			String album = tag.getFirst(FieldKey.ALBUM);
			int trackNumber = Integer.parseInt(tag.getFirst(FieldKey.TRACK));
			String track = tag.getFirst(FieldKey.TITLE);
			trackInformation = new TrackInformation(artist, album, trackNumber, track);
		}
		catch (NumberFormatException e) {
			log.warn("Could not read track information from " + file, e);
		}
		catch (KeyNotFoundException e) {
			log.warn("Could not read track information from " + file, e);
		}
		catch (CannotReadException e) {
			log.warn("Could not read track information from " + file, e);
		}
		catch (IOException e) {
			log.warn("Could not read track information from " + file, e);
		}
		catch (TagException e) {
			log.warn("Could not read track information from " + file, e);
		}
		catch (ReadOnlyFileException e) {
			log.warn("Could not read track information from " + file, e);
		}
		catch (InvalidAudioFrameException e) {
			log.warn("Could not read track information from " + file, e);
		}
		return trackInformation;
	}

	protected class TrackInformation {
		
		private String i_artist;
		private String i_album;
		private int i_trackNumber;
		private String i_track;
		
		public TrackInformation(String artist, String album, int trackNumber, String track) {
			super();
			i_artist = artist;
			i_album = album;
			i_trackNumber = trackNumber;
			i_track = track;
		}

		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this);
		}
		
		@Override
		public boolean equals(Object obj) {
			return EqualsBuilder.reflectionEquals(this, obj);
		}
		
		@Override
		public String toString() {
			return String.format("%s, %s: %2d - %s", getArtist(), getAlbum(), getTrackNumber(), getTrack());
		}
		
		public String getArtist() {
			return i_artist;
		}

		public String getAlbum() {
			return i_album;
		}

		public int getTrackNumber() {
			return i_trackNumber;
		}

		public String getTrack() {
			return i_track;
		}
	}

	public SortedSet<Encoding> getEncodings() {
		return i_encodings;
	}

	public FileService getFileService() {
		return i_fileService;
	}

	public File getFlacDirectory() {
		return i_flacDirectory;
	}
}
