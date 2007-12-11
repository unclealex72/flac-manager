package uk.co.unclealex.music.encoder.encoded.service;

import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.encoder.flac.model.FlacTrackBean;

public class EncodingEvent {

	private FlacTrackBean i_flacTrackBean;
	private EncoderBean i_encoderBean;
	
	public EncodingEvent(
			FlacTrackBean flacTrackBean, EncoderBean encoderBean) {
		super();
		i_flacTrackBean = flacTrackBean;
		i_encoderBean = encoderBean;
	}
	
	public FlacTrackBean getFlacTrackBean() {
		return i_flacTrackBean;
	}
	
	public EncoderBean getEncoderBean() {
		return i_encoderBean;
	}
	
}
