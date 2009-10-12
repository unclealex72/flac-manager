package uk.co.unclealex.music.encoder.listener;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.albumcover.service.AlbumCoverService;
import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.service.ArtworkTaggingException;
import uk.co.unclealex.music.base.service.ExternalCoverArtException;
import uk.co.unclealex.music.encoder.action.AlbumCoverAddedAction;
import uk.co.unclealex.music.encoder.action.AlbumCoverRemovedAction;
import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.action.NoAlbumCoversFoundAction;
import uk.co.unclealex.music.encoder.exception.EventException;

@Transactional
public class AlbumCoverEncodingEventListener extends AbstractEncodingEventListener {

	private static final Logger log = Logger.getLogger(AlbumCoverEncodingEventListener.class);
	
	private AlbumCoverService i_albumCoverService;
	
	@Override
	public void albumAdded(
			FlacAlbumBean flacAlbumBean, EncodedAlbumBean encodedAlbumBean, 
			final List<EncodingAction> encodingActions) throws EventException {
		AlbumCoverService albumCoverService = getAlbumCoverService();
		try {
			if (albumCoverService.downloadCoversForAlbum(flacAlbumBean).isEmpty()) {
				encodingActions.add(new NoAlbumCoversFoundAction(encodedAlbumBean));
			}
			else {
				encodingActions.add(new AlbumCoverAddedAction(encodedAlbumBean));
			}
		}
		catch (ExternalCoverArtException e) {
			// Just warn about this
			log.warn("Could not download any covers for album " + flacAlbumBean, e);
		}
		catch (IOException e) {
			// Just warn about this
			log.warn("Could not download any covers for album " + flacAlbumBean, e);
		}
		catch (ArtworkTaggingException e) {
			// Just warn about this
			log.warn("Could not download any covers for album " + flacAlbumBean, e);
		}
	}
	
	@Override
	public void albumRemoved(EncodedAlbumBean encodedAlbumBean, final List<EncodingAction> encodingActions) throws EventException {
		AlbumCoverService albumCoverService = getAlbumCoverService();
		if (albumCoverService.removeCoversForMissingAlbum(
				encodedAlbumBean.getEncodedArtistBean().getCode(), encodedAlbumBean.getCode())) {
			encodingActions.add(new AlbumCoverRemovedAction(encodedAlbumBean));	
		}
	}

	public AlbumCoverService getAlbumCoverService() {
		return i_albumCoverService;
	}

	public void setAlbumCoverService(AlbumCoverService albumCoverService) {
		i_albumCoverService = albumCoverService;
	}
}
