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

public interface EncodingEventListener {

	public void encodingStarted();
	public void trackAdded(FlacTrackBean flacTrackBean, EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions) throws EventException;
	public void albumAdded(FlacAlbumBean flacAlbumBean, EncodedAlbumBean encodedAlbumBean, List<EncodingAction> encodingActions) throws EventException;
	public void artistAdded(FlacArtistBean flacArtistBean, EncodedArtistBean encodedArtistBean, List<EncodingAction> encodingActions) throws EventException;
	public void ownerAdded(OwnerBean ownerBean, EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions) throws EventException;

	public void encodingFinished();
	public void trackRemoved(EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions) throws EventException;
	public void albumRemoved(EncodedAlbumBean encodedAlbumBean, List<EncodingAction> encodingActions) throws EventException;
	public void artistRemoved(EncodedArtistBean encodedArtistBean, List<EncodingAction> encodingActions) throws EventException;
	public void ownerRemoved(
			OwnerBean ownerBean, EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions) throws EventException;
	public boolean isSynchronisationRequired();
}
