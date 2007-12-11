package uk.co.unclealex.music.web.converter;

import uk.co.unclealex.flacconverter.KeyedConverter;
import uk.co.unclealex.music.web.encoded.dao.EncodedTrackDao;
import uk.co.unclealex.music.web.encoded.dao.KeyedDao;
import uk.co.unclealex.music.web.encoded.model.EncodedTrackBean;

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
