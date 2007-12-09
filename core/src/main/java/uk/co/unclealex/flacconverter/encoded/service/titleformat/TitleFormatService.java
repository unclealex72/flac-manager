package uk.co.unclealex.flacconverter.encoded.service.titleformat;

import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;

public interface TitleFormatService {

	public String getTitle(EncodedTrackBean encodedTrackBean);

	public void setTitleFormat(String titleFormat);
}
