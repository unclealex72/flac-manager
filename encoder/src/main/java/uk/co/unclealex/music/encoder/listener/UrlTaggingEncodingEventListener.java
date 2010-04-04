package uk.co.unclealex.music.encoder.listener;

import java.io.IOException;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import uk.co.unclealex.hibernate.model.DataBean;
import uk.co.unclealex.hibernate.service.DataService;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.action.TrackCommentedAction;
import uk.co.unclealex.music.encoder.exception.EventException;

public class UrlTaggingEncodingEventListener extends AbstractEncodingEventListener {

	private DataService i_dataService;
	
	@Override
	public void trackAdded(FlacTrackBean flacTrackBean, EncodedTrackBean encodedTrackBean,
			List<EncodingAction> encodingActions) throws EventException {
		String url = flacTrackBean.getUrl();
		try {
			DataBean trackDataBean = encodedTrackBean.getTrackDataBean();
			AudioFile audioFile = AudioFileIO.read(getDataService().findFile(trackDataBean));
			Tag tag = audioFile.getTag();
			tag.setComment(url);
			audioFile.commit();
			encodingActions.add(new TrackCommentedAction(encodedTrackBean, url));
		}
		catch (FieldDataInvalidException e) {
			throw new EventException("Could not tag " + encodedTrackBean + " with url " + url, e);
		}
		catch (CannotReadException e) {
			throw new EventException("Could not tag " + encodedTrackBean + " with url " + url, e);
		}
		catch (IOException e) {
			throw new EventException("Could not tag " + encodedTrackBean + " with url " + url, e);
		}
		catch (TagException e) {
			throw new EventException("Could not tag " + encodedTrackBean + " with url " + url, e);
		}
		catch (ReadOnlyFileException e) {
			throw new EventException("Could not tag " + encodedTrackBean + " with url " + url, e);
		}
		catch (InvalidAudioFrameException e) {
			throw new EventException("Could not tag " + encodedTrackBean + " with url " + url, e);
		}
		catch (CannotWriteException e) {
			throw new EventException("Could not tag " + encodedTrackBean + " with url " + url, e);
		}
	}

	public DataService getDataService() {
		return i_dataService;
	}

	public void setDataService(DataService dataService) {
		i_dataService = dataService;
	}
}
