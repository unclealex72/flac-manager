package uk.co.unclealex.music.core.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.model.EncodedArtistBean;

@Repository("encodedArtistDao")
@Transactional
public class HibernateEncodedArtistDao extends
		HibernateKeyedDao<EncodedArtistBean> implements EncodedArtistDao {

	@Override
	public EncodedArtistBean createExampleBean() {
		return new EncodedArtistBean();
	}

	@Override
	public EncodedArtistBean findByName(String name) {
		EncodedArtistBean artistBean = createExampleBean();
		artistBean.setName(name);
		return (EncodedArtistBean) createCriteria(artistBean).uniqueResult();
	}
	
}
