package uk.co.unclealex.music.web.converter;

import uk.co.unclealex.music.core.dao.EncoderDao;
import uk.co.unclealex.music.core.dao.KeyedReadOnlyDao;
import uk.co.unclealex.music.core.model.EncoderBean;

public class EncoderConverter extends KeyedConverter<EncoderBean> {

	private EncoderDao i_encoderDao;
	
	@Override
	protected KeyedReadOnlyDao<EncoderBean> getDao() {
		return getEncoderDao();
	}

	public EncoderDao getEncoderDao() {
		return i_encoderDao;
	}

	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

}
