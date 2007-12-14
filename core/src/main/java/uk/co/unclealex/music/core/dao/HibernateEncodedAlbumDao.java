package uk.co.unclealex.music.core.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;

@Repository("encodedAlbumDao")
@Transactional
public class HibernateEncodedAlbumDao extends
		HibernateKeyedDao<EncodedAlbumBean> implements EncodedAlbumDao {

	@Override
	public EncodedAlbumBean createExampleBean() {
		return new EncodedAlbumBean();
	}

	@Override
	public EncodedAlbumBean findByArtistAndTitle(String artistName,
			String albumTitle) {
		Query query = getSession().createQuery(
				"select al from encodedArtistBean ar join ar.encodedAlbumBeans al " +
				"where ar.name = :name and al.title = :title").
			setString("name", artistName).
			setString("title", albumTitle);
		return (EncodedAlbumBean) query.uniqueResult();
	}

	
}
