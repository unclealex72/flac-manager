package uk.co.unclealex.music.core.dao;

import java.util.SortedSet;

import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;
import uk.co.unclealex.music.base.dao.EncodedArtistDao;
import uk.co.unclealex.music.base.model.EncodedArtistBean;

@Transactional
public class HibernateEncodedArtistDao extends
		HibernateKeyedDao<EncodedArtistBean> implements EncodedArtistDao {

	@Override
	public EncodedArtistBean createExampleBean() {
		return new EncodedArtistBean();
	}

	@Override
	public EncodedArtistBean findByCode(String code) {
		EncodedArtistBean artistBean = createExampleBean();
		artistBean.setCode(code);
		return uniqueResult(createCriteria(artistBean));
	}

	@Override
	public EncodedArtistBean findByFilename(String filename) {
		EncodedArtistBean artistBean = createExampleBean();
		artistBean.setFilename(filename);
		return uniqueResult(createCriteria(artistBean));
	}

	@Override
	public SortedSet<EncodedArtistBean> findByFirstLetter(char firstLetter) {
		Query query = getSession().createQuery("from encodedArtistBean where code like :firstLetter");
		query.setString("firstLetter", firstLetter + "%");
		return asSortedSet(query);
	}
	
	@Override
	public SortedSet<EncodedArtistBean> findAllEmptyArtists() {
		Query query = getSession().createQuery(
				"from encodedArtistBean a where a.encodedAlbumBeans is empty");
		return asSortedSet(query);
	}
}
