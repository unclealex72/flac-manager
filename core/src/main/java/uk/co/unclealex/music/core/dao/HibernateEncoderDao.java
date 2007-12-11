package uk.co.unclealex.music.core.dao;

import uk.co.unclealex.music.core.model.EncoderBean;

public class HibernateEncoderDao extends HibernateEncodingDao<EncoderBean>
		implements EncoderDao {

	@Override
	public EncoderBean findByExtension(String extension) {
		EncoderBean encoderBean = createExampleBean();
		encoderBean.setExtension(extension);
		return (EncoderBean) createCriteria(encoderBean).uniqueResult();
	}
	
	@Override
	public EncoderBean createExampleBean() {
		return new EncoderBean();
	}
}
