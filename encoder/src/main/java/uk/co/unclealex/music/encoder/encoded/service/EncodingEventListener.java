package uk.co.unclealex.music.encoder.encoded.service;

import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.encoder.flac.model.FlacTrackBean;

public interface EncodingEventListener {

	public void afterTrackEncoded(EncodedTrackBean encodedTrackBean, FlacTrackBean flacTrackBean);
	
	public void beforeTrackRemoved(EncodedTrackBean encodedTrackBean);
}
