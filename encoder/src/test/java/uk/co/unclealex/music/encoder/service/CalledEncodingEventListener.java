package uk.co.unclealex.music.encoder.service;

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
import uk.co.unclealex.music.encoder.listener.EncodingEventListener;

public class CalledEncodingEventListener implements EncodingEventListener {

	private boolean i_called = false;
	
	@Override
	public void albumAdded(FlacAlbumBean flacAlbumBean, EncodedAlbumBean encodedAlbumBean,
			List<EncodingAction> encodingActions) throws EventException {
		setCalled(true);
	}

	@Override
	public void albumRemoved(EncodedAlbumBean encodedAlbumBean, List<EncodingAction> encodingActions)
			throws EventException {
		setCalled(true);
	}

	@Override
	public void artistAdded(FlacArtistBean flacArtistBean, EncodedArtistBean encodedArtistBean,
			List<EncodingAction> encodingActions) throws EventException {
		setCalled(true);
	}

	@Override
	public void artistRemoved(EncodedArtistBean encodedArtistBean, List<EncodingAction> encodingActions)
			throws EventException {
		setCalled(true);
	}

	@Override
	public void encodingFinished() {
		setCalled(true);
	}

	@Override
	public void encodingStarted() {
		setCalled(true);
	}

	@Override
	public void ownerAdded(OwnerBean ownerBean, EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions)
			throws EventException {
		setCalled(true);
	}

	@Override
	public void ownerRemoved(OwnerBean ownerBean, EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions)
			throws EventException {
		setCalled(true);
	}

	@Override
	public void trackAdded(FlacTrackBean flacTrackBean, EncodedTrackBean encodedTrackBean,
			List<EncodingAction> encodingActions) throws EventException {
		setCalled(true);
	}

	@Override
	public void trackRemoved(EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions)
			throws EventException {
		setCalled(true);
	}

	public boolean isCalled() {
		return i_called;
	}

	public void setCalled(boolean called) {
		i_called = called;
	}

}
