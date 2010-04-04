package uk.co.unclealex.music.albumcover.service;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.datatype.Artwork;

import uk.co.unclealex.music.base.service.ArtworkTaggingException;

public class JAudioTaggerArtworkTaggingService implements ArtworkTaggingService {

	private static final Logger log = Logger.getLogger(JAudioTaggerArtworkTaggingService.class);
	
	@Override
	public void updateTag(File albumCoverFile, File trackFile, String trackName, boolean updateTimestamp) throws IOException, ArtworkTaggingException {
		log.info("Adding album cover tag to " + trackName + " (" + trackFile.getAbsolutePath() + ")");
		try {
			long lastModified = trackFile.lastModified();
			AudioFile audioFile = AudioFileIO.read(trackFile);
			Tag tag = audioFile.getTag();
			tag.deleteArtworkField();
			Artwork artwork = Artwork.createArtworkFromFile(albumCoverFile);
			artwork.setDescription("");
			tag.createField(artwork);
			audioFile.commit();
			if (!updateTimestamp) {
				trackFile.setLastModified(lastModified);
			}
		}
		catch (KeyNotFoundException e) {
			throw new ArtworkTaggingException("Cannot tag " + trackName, e);
		}
		catch (FieldDataInvalidException e) {
			throw new ArtworkTaggingException("Cannot tag " + trackName, e);
		}
		catch (CannotReadException e) {
			throw new ArtworkTaggingException("Cannot tag " + trackName, e);
		}
		catch (TagException e) {
			throw new ArtworkTaggingException("Cannot tag " + trackName, e);
		}
		catch (ReadOnlyFileException e) {
			throw new ArtworkTaggingException("Cannot tag " + trackName, e);
		}
		catch (InvalidAudioFrameException e) {
			throw new ArtworkTaggingException("Cannot tag " + trackName, e);
		}
		catch (CannotWriteException e) {
			throw new ArtworkTaggingException("Cannot tag " + trackName, e);
		}
	}

}
