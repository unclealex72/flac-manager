package uk.co.unclealex.flacconverter.encoded.service;

import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;

public interface EncodingEventListener {

	public void afterTrackEncoded(EncodedTrackBean encodedTrackBean, FlacTrackBean flacTrackBean);
	
	public void beforeTrackRemoved(EncodedTrackBean encodedTrackBean);
}
