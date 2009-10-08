package uk.co.unclealex.music.encoder.listener;

import java.util.List;

import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacArtistBean;
import uk.co.unclealex.music.encoder.action.AlbumAddedAction;
import uk.co.unclealex.music.encoder.action.AlbumRemovedAction;
import uk.co.unclealex.music.encoder.action.ArtistAddedAction;
import uk.co.unclealex.music.encoder.action.ArtistRemovedAction;
import uk.co.unclealex.music.encoder.action.EncodingAction;
import uk.co.unclealex.music.encoder.action.TrackRemovedAction;
import uk.co.unclealex.music.encoder.exception.EventException;

public class AlbumArtistChangeEventListener extends AbstractEncodingEventListener {

	@Override
	public void albumAdded(FlacAlbumBean flacAlbumBean, EncodedAlbumBean encodedAlbumBean,
			List<EncodingAction> encodingActions) throws EventException {
		encodingActions.add(new AlbumAddedAction(encodedAlbumBean));
	}
	
	@Override
	public void albumRemoved(EncodedAlbumBean encodedAlbumBean, List<EncodingAction> encodingActions) throws EventException {
		encodingActions.add(new AlbumRemovedAction(encodedAlbumBean));
	}
	
	@Override
	public void artistAdded(FlacArtistBean flacArtistBean, EncodedArtistBean encodedArtistBean,
			List<EncodingAction> encodingActions) throws EventException {
		encodingActions.add(new ArtistAddedAction(encodedArtistBean));
	}
	
	@Override
	public void artistRemoved(EncodedArtistBean encodedArtistBean, List<EncodingAction> encodingActions)
			throws EventException {
		encodingActions.add(new ArtistRemovedAction(encodedArtistBean));
	}
	
	@Override
	public void trackRemoved(EncodedTrackBean encodedTrackBean, List<EncodingAction> encodingActions) throws EventException {
		encodingActions.add(new TrackRemovedAction(encodedTrackBean));
	}
}
