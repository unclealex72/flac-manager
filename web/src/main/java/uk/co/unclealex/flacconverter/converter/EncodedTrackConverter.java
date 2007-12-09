package uk.co.unclealex.flacconverter.converter;

import uk.co.unclealex.flacconverter.encoded.dao.EncodedTrackDao;
import uk.co.unclealex.flacconverter.encoded.dao.KeyedDao;
import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;

public class EncodedTrackConverter extends KeyedConverter<EncodedTrackBean> {

	private EncodedTrackDao i_encodedTrackDao;
	
	@Override
	protected KeyedDao<EncodedTrackBean> getDao() {
		return getEncodedTrackDao();
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

}
