package uk.co.unclealex.music.core.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.model.EncodedArtistBean;

@Repository
@Transactional
public class HibernateEncodedArtistDao extends
		HibernateKeyedDao<EncodedArtistBean> implements EncodedArtistDao {

	@Autowired
	public HibernateEncodedArtistDao(@Qualifier("musicSessionFactory") SessionFactory sessionFactory) {
		super(sessionFactory);
	}


	@Override
	public EncodedArtistBean createExampleBean() {
		return new EncodedArtistBean();
	}

	@Override
	public EncodedArtistBean findByIdentifier(String identifier) {
		EncodedArtistBean artistBean = createExampleBean();
		artistBean.setIdentifier(identifier);
		return (EncodedArtistBean) createCriteria(artistBean).uniqueResult();
	}
	
}
