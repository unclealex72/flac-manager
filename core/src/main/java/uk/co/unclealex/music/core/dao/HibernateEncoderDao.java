package uk.co.unclealex.music.core.dao;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;
import uk.co.unclealex.music.base.dao.EncoderDao;
import uk.co.unclealex.music.base.model.EncoderBean;

@Transactional
public class HibernateEncoderDao extends HibernateKeyedDao<EncoderBean>
		implements EncoderDao {

	@Override
	public EncoderBean findByExtension(String extension) {
		EncoderBean encoderBean = createExampleBean();
		encoderBean.setExtension(extension);
		return uniqueResult(createCriteria(encoderBean));
	}
	
	@Override
	public EncoderBean createExampleBean() {
		return new EncoderBean();
	}
}
