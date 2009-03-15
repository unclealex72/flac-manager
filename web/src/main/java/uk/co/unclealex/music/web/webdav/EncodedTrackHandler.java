package uk.co.unclealex.music.web.webdav;

import uk.co.unclealex.music.base.model.EncodedTrackBean;

public class EncodedTrackHandler extends AbstractHandler<EncodedTrackBean> {

	@Override
	public String getDataExtractorBeanName() {
		return "encodedTrackDataExtractor";
	}

	
}
