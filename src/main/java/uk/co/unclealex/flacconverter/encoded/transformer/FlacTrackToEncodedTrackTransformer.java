package uk.co.unclealex.flacconverter.encoded.transformer;

import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

public class FlacTrackToEncodedTrackTransformer extends
		ToEncodedTracksTransformer<FlacTrackBean, EncodedTrackBean> {

	@Override
	public EncodedTrackBean transform(FlacTrackBean flactrackBean) {
		return getEncodedTrackDao().findByUrlAndEncoderBean(flactrackBean.getUrl(), getEncoderBean());
	}
}
