package uk.co.unclealex.music.web.converter;

import com.opensymphony.xwork2.conversion.annotations.Conversion;

import uk.co.unclealex.hibernate.dao.KeyedReadOnlyDao;
import uk.co.unclealex.music.base.dao.EncodedTrackDao;
import uk.co.unclealex.music.base.model.EncodedTrackBean;

@Conversion
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
