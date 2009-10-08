package uk.co.unclealex.music.encoder.listener;

import java.util.List;

import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacArtistBean;
import uk.co.unclealex.music.base.model.FlacTrackBean;
import uk.co.unclealex.music.base.model.OwnerBean;
import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.exception.EventException;

public abstract class AbstractEncodingEventListener implements EncodingEventListener {

	@Override
	public void encodingStarted() {
		// Do nothing
	}
	
	@Override
	public void encodingFinished() {
		// Do nothing
	}
	
	@Override
	public void albumAdded(FlacAlbumBean flacAlbumBean, EncodedAlbumBean encodedAlbumBean, List<EncodingAction> encodingActions) throws EventException {
		// Do nothing
	}

	@Override
	public void artistAdded(FlacArtistBean flacArtistBean, EncodedArtistBean encodedArtistBean, List<EncodingAction> encodingActions) throws EventException {
		// Do nothing
	}

	
	@Override
	public void trackAdded(FlacTrackBean flacTrackBean, EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions) throws EventException {
		// Do nothing
	}

	@Override
	public void ownerAdded(OwnerBean ownerBean, EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions) throws EventException {
		// Do nothing
	}

	@Override
	public void albumRemoved(EncodedAlbumBean encodedAlbumBean, List<EncodingAction> encodingActions)
			throws EventException {
		// Do nothing
	}

	@Override
	public void artistRemoved(EncodedArtistBean encodedArtistBean, List<EncodingAction> encodingActions)
			throws EventException {
		// Do nothing
	}

	@Override
	public void trackRemoved(EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions)
			throws EventException {
		// Do nothing
	}

	@Override
	public void ownerRemoved(OwnerBean ownerBean, EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions)
			throws EventException {
		// Do nothing
	}
	
	@Override
	public boolean isSynchronisationRequired() {
		return true;
	}
}
