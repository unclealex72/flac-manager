package uk.co.unclealex.music.core.dao;

import java.util.SortedSet;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;

@Repository
@Transactional
public class HibernateEncodedAlbumDao extends
		HibernateKeyedDao<EncodedAlbumBean> implements EncodedAlbumDao {

	@Autowired
	public HibernateEncodedAlbumDao(@Qualifier("musicSessionFactory") SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public EncodedAlbumBean createExampleBean() {
		return new EncodedAlbumBean();
	}

	@Override
	public EncodedAlbumBean findByArtistAndIdentifier(EncodedArtistBean encodedArtistBean,
			String albumIdentifier) {
		Query query = getSession().createQuery(
				"select al from encodedArtistBean ar join ar.encodedAlbumBeans al " +
				"where ar = :artist and al.identifier = :identifier").
			setEntity("artist", encodedArtistBean).
			setString("identifier", albumIdentifier);
		return (EncodedAlbumBean) query.uniqueResult();
	}

	@Override
	public SortedSet<EncodedAlbumBean> findAllEmptyAlbums() {
		Query query = getSession().createQuery(
				"from encodedAlbumBean a where a.encodedTrackBeans is empty");
		return asSortedSet(query);
	}
	
}
