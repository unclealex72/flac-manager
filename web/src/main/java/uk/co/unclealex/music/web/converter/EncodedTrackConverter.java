package uk.co.unclealex.music.web.converter;

import uk.co.unclealex.music.core.dao.EncodedTrackDao;
import uk.co.unclealex.music.core.dao.KeyedReadOnlyDao;
import uk.co.unclealex.music.core.model.EncodedTrackBean;

public class EncodedTrackConverter extends KeyedConverter<EncodedTrackBean> {

	private EncodedTrackDao i_encodedTrackDao;
	
	@Override
	protected KeyedReadOnlyDao<EncodedTrackBean> getDao() {
		return getEncodedTrackDao();
	}

	public EncodedTrackDao getEncodedTrackDao() {
		return i_encodedTrackDao;
	}

	public void setEncodedTrackDao(EncodedTrackDao encodedTrackDao) {
		i_encodedTrackDao = encodedTrackDao;
	}

}
