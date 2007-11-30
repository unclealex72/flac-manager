package uk.co.unclealex.flacconverter.encoded.service;

import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

public interface EncodingEventListener {

	public void afterTrackEncoded(EncodedTrackBean encodedTrackBean, FlacTrackBean flacTrackBean);
	
	public void beforeTrackRemoved(EncodedTrackBean encodedTrackBean);
}
