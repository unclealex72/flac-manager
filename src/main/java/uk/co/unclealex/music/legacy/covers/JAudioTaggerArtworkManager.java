package uk.co.unclealex.music.legacy.covers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.datatype.Artwork;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

public class JAudioTaggerArtworkManager<F extends AudioFile> implements ArtworkManager {

	@Override
	public boolean artworkExists(File audioFile) throws IOException {
		return getArtworkFromFile(audioFile) != null;
	}

	@Override
	public byte[] getArtwork(File audioFile) throws IOException {
		Artwork artwork = getArtworkFromFile(audioFile);
		return artwork == null?null:artwork.getBinaryData();
	}

	protected Artwork getArtworkFromFile(File trackFile) throws IOException {
		F audioFile = readAudioFile(trackFile);
		Tag tag = getTag(audioFile);
		return tag.getFirstArtwork();
	}

	@SuppressWarnings("unchecked")
	protected F readAudioFile(File trackFile) throws IOException {
		try {
			return (F) AudioFileIO.read(trackFile);
		}
		catch (CannotReadException e) {
			throw new IOException("Cannot read tag information from audio file " + trackFile, e);
		}
		catch (TagException e) {
			throw new IOException("Cannot read tag information from audio file " + trackFile, e);
		}
		catch (ReadOnlyFileException e) {
			throw new IOException("Cannot read tag information from audio file " + trackFile, e);
		}
		catch (InvalidAudioFrameException e) {
			throw new IOException("Cannot read tag information from audio file " + trackFile, e);
		}
	}
	
	@Override
	public void setArtwork(byte[] artworkData, File... audioFiles) throws IOException {
		File tempFile = File.createTempFile("artwork-", null);
		tempFile.deleteOnExit();
		OutputStream out = null;
		try {
			out = new FileOutputStream(tempFile);
			ByteStreams.copy(new ByteArrayInputStream(artworkData), out);
			Closeables.closeQuietly(out);
			Artwork artwork = Artwork.createArtworkFromFile(tempFile);
			for (File audioFile : audioFiles) {
				setArtwork(audioFile, artwork);
			}
		}
		finally {
			Closeables.closeQuietly(out);
			tempFile.delete();
		}
	}

	protected void setArtwork(File trackFile, Artwork artwork) throws IOException {
		F audioFile = readAudioFile(trackFile);
		Tag tag = getTag(audioFile);
		tag.deleteArtworkField();
		artwork.setDescription("");
		try {
			tag.addField(artwork);
			audioFile.commit();
		}
		catch (FieldDataInvalidException e) {
			throw new IOException("Cannot write tag information to audio file " + trackFile, e);
		}
		catch (CannotWriteException e) {
			throw new IOException("Cannot write tag information to audio file " + trackFile, e);
		}
	}

	protected Tag getTag(F audioFile) {
		return audioFile.getTag();
	}

}
