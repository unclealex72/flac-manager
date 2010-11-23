package uk.co.unclealex.music.encoding;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import uk.co.unclealex.music.Constants;

public class RenamingServiceImpl implements RenamingService {

	private FileFixingService i_fileFixingService;
	
	@Override
	public void merge(String albumTitle, Set<File> flacDirectories) throws IOException {
		Set<File> flacFiles = new LinkedHashSet<File>();
		for (File flacDirectory : flacDirectories) {
			@SuppressWarnings("unchecked")
			Collection<File> files = FileUtils.listFiles(flacDirectory, new String[] { Constants.FLAC }, false);
			flacFiles.addAll(new TreeSet<File>(files));
		}
		int trackNumber = 1;
		for (File flacFile : flacFiles) {
			alterFlacFile(flacFile, null, albumTitle, null, trackNumber, null);
			trackNumber++;
		}
		getFileFixingService().fixFlacFilenames(flacFiles);
	}

	@Override
	public void rename(Set<File> flacFiles, String artist, String album, Boolean compilation, Integer trackNumber, String title)
			throws IOException {
		for (File flacFile : flacFiles) {
			alterFlacFile(flacFile, artist, album, compilation, trackNumber, title);
		}
		getFileFixingService().fixFlacFilenames(flacFiles);
	}
	
	protected void alterFlacFile(File flacFile, String artist, String album, Boolean compilation, Integer trackNumber, String title) throws IOException {
		try {
			AudioFile flacAudioFile = AudioFileIO.read(flacFile);
			Tag tag = flacAudioFile.getTag();
			alterTag(tag, FieldKey.ARTIST, artist);
			alterTag(tag, FieldKey.ALBUM, album);
			alterTag(tag, FieldKey.TRACK, trackNumber);
			alterTag(tag, FieldKey.TITLE, title);
			if (compilation != null) {
				boolean isCompilation = compilation.booleanValue();
				if (isCompilation) {
					tag.setField(FieldKey.ALBUM_ARTIST, Constants.VARIOUS_ARTISTS);
				}
				else {
					tag.deleteField(FieldKey.ALBUM_ARTIST);
				}
			}
			flacAudioFile.commit();
		}
		catch (KeyNotFoundException e) {
			throw new IOException(e);
		}
		catch (FieldDataInvalidException e) {
			throw new IOException(e);
		}
		catch (CannotReadException e) {
			throw new IOException(e);
		}
		catch (TagException e) {
			throw new IOException(e);
		}
		catch (ReadOnlyFileException e) {
			throw new IOException(e);
		}
		catch (InvalidAudioFrameException e) {
			throw new IOException(e);
		}
		catch (CannotWriteException e) {
			throw new IOException(e);
		}
	}
	
	protected void alterTag(Tag tag, FieldKey fieldKey, Object value) throws KeyNotFoundException, FieldDataInvalidException {
		if (value != null) {
			String fieldValue;
			if (value instanceof Integer) {
				fieldValue = String.format("%02d", value);
			}
			else {
				fieldValue = value.toString().trim();
			}
			tag.setField(fieldKey, fieldValue);
		}
	}

	public FileFixingService getFileFixingService() {
		return i_fileFixingService;
	}

	public void setFileFixingService(FileFixingService fileFixingService) {
		i_fileFixingService = fileFixingService;
	}

}
