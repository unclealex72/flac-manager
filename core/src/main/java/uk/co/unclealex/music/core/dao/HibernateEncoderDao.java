package uk.co.unclealex.music.core.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.model.EncoderBean;

@Repository
@Transactional
public class HibernateEncoderDao extends HibernateKeyedDao<EncoderBean>
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
