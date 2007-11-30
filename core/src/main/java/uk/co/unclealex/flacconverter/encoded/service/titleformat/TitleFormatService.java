package uk.co.unclealex.flacconverter.encoded.service.titleformat;

import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

public interface TitleFormatService {

	public String getTitle(EncodedTrackBean encodedTrackBean);

	public String getTitle(FlacTrackBean flacTrackBean, EncoderBean encoderBean);
	
	public void setTitleFormat(String titleFormat);
}
