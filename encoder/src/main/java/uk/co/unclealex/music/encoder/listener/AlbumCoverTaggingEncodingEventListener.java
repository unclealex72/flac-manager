package uk.co.unclealex.music.encoder.listener;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.albumcover.service.AlbumCoverService;
import uk.co.unclealex.music.base.dao.AlbumCoverDao;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.service.ArtworkTaggingException;
import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.action.TrackTaggedAction;
import uk.co.unclealex.music.encoder.exception.EventException;

@Transactional
public class AlbumCoverTaggingEncodingEventListener extends AbstractEncodingEventListener {

	private static final Logger log = Logger.getLogger(AlbumCoverTaggingEncodingEventListener.class);
	
	private AlbumCoverService i_albumCoverService;
	private AlbumCoverDao i_albumCoverDao;

	@Override
	public void trackAdded(FlacTrackBean flacTrackBean, EncodedTrackBean encodedTrackBean,
			List<EncodingAction> encodingActions) throws EventException {
		try {
			if (getAlbumCoverService().tagFile(encodedTrackBean, false)) {
				encodingActions.add(new TrackTaggedAction(encodedTrackBean));
			}
		}
		catch (IOException e) {
			log.warn("Could not tag " + encodedTrackBean, e);
		}
		catch (ArtworkTaggingException e) {
			log.warn("Could not tag " + encodedTrackBean, e);
		}
	}
	
	public AlbumCoverService getAlbumCoverService() {
		return i_albumCoverService;
	}

	public void setAlbumCoverService(AlbumCoverService albumCoverService) {
		i_albumCoverService = albumCoverService;
	}

	public AlbumCoverDao getAlbumCoverDao() {
		return i_albumCoverDao;
	}

	public void setAlbumCoverDao(AlbumCoverDao albumCoverDao) {
		i_albumCoverDao = albumCoverDao;
	}
}
