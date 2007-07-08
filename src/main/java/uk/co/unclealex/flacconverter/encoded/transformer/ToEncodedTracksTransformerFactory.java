package uk.co.unclealex.flacconverter.encoded.transformer;

import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;

public interface ToEncodedTracksTransformerFactory {

	public FlacTrackToEncodedTrackTransformer createFlacTrackToEncodedTrackTransformer(EncoderBean encoderBean);
	public FlacAlbumToEncodedTracksTransformer createFlacAlbumToEncodedTracksTransformer(EncoderBean encoderBean);
}
