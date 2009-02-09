package uk.co.unclealex.music.web.converter;

import com.opensymphony.xwork2.conversion.annotations.Conversion;

import uk.co.unclealex.music.base.dao.EncoderDao;
import uk.co.unclealex.music.base.dao.KeyedReadOnlyDao;
import uk.co.unclealex.music.base.model.EncoderBean;

@Conversion
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
