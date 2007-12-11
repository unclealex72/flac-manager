package uk.co.unclealex.music.web.converter;

import uk.co.unclealex.flacconverter.KeyedConverter;
import uk.co.unclealex.music.web.encoded.dao.EncoderDao;
import uk.co.unclealex.music.web.encoded.dao.KeyedDao;
import uk.co.unclealex.music.web.encoded.model.EncoderBean;

public class EncoderConverter extends KeyedConverter<EncoderBean> {

	private EncoderDao i_encoderDao;
	
	@Override
	protected KeyedDao<EncoderBean> getDao() {
		return getEncoderDao();
	}

	public EncoderDao getEncoderDao() {
		return i_encoderDao;
	}

	public void setEncoderDao(EncoderDao encoderDao) {
		i_encoderDao = encoderDao;
	}

}
