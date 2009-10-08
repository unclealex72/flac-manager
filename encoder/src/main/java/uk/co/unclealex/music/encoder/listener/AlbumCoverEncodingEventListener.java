package uk.co.unclealex.music.encoder.listener;

import java.util.Collections;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.albumcover.service.AlbumCoverService;
import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.encoder.action.AlbumCoverAddedAction;
import uk.co.unclealex.music.encoder.action.AlbumCoverRemovedAction;
import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.exception.EventException;

@Transactional
public class AlbumCoverEncodingEventListener extends AbstractEncodingEventListener {

	private AlbumCoverService i_albumCoverService;
	
	@Override
	public void albumAdded(
			FlacAlbumBean flacAlbumBean, EncodedAlbumBean encodedAlbumBean, 
			final List<EncodingAction> encodingActions) throws EventException {
		AlbumCoverService albumCoverService = getAlbumCoverService();
		if (albumCoverService != null) {
			albumCoverService.downloadAndSaveCoversForAlbums(Collections.singleton(flacAlbumBean));
			encodingActions.add(new AlbumCoverAddedAction(encodedAlbumBean));
		}
	}
	
	@Override
	public void albumRemoved(EncodedAlbumBean encodedAlbumBean, final List<EncodingAction> encodingActions) throws EventException {
		AlbumCoverService albumCoverService = getAlbumCoverService();
		if (albumCoverService != null) {
			albumCoverService.removeCoversForMissingAlbum(encodedAlbumBean.getEncodedArtistBean().getCode(), encodedAlbumBean.getCode());
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
