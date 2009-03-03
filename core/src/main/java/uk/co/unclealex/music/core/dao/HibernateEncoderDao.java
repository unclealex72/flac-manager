package uk.co.unclealex.music.core.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;
import uk.co.unclealex.music.base.dao.EncoderDao;
import uk.co.unclealex.music.base.model.EncoderBean;

@Repository
@Transactional
public class HibernateEncoderDao extends HibernateKeyedDao<EncoderBean>
		implements EncoderDao {

	@Autowired
	public HibernateEncoderDao(@Qualifier("musicSessionFactory") SessionFactory sessionFactory) {
		super(sessionFactory);
	}

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
