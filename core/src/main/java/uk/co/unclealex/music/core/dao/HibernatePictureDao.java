package uk.co.unclealex.music.core.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.model.PictureBean;

@Repository
@Transactional
public class HibernatePictureDao extends HibernateKeyedDao<PictureBean> implements PictureDao {

	@Autowired
	public HibernatePictureDao(@Qualifier("musicSessionFactory") SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public PictureBean createExampleBean() {
		return new PictureBean();
	}
}
