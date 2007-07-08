package uk.co.unclealex.flacconverter.encoded.transformer;

import org.apache.commons.collections15.Transformer;

import uk.co.unclealex.flacconverter.encoded.dao.EncodedTrackDao;
import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;

public abstract class ToEncodedTracksTransformer<I, O> implements Transformer<I, O> {

	private EncoderBean i_encoderBean;
	private EncodedTrackDao i_encodedTrackDao;
	
	public EncoderBean getEncoderBean() {
		return i_encoderBean;
	}

	public void setEncoderBean(EncoderBean encoderBean) {
		i_encoderBean = encoderBean;
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}
	
}
