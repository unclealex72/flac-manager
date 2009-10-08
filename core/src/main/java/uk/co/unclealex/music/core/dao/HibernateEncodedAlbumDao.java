package uk.co.unclealex.music.core.dao;

import java.util.SortedSet;

import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;
import uk.co.unclealex.music.base.dao.EncodedAlbumDao;
import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;

@Transactional
public class HibernateEncodedAlbumDao extends
		HibernateKeyedDao<EncodedAlbumBean> implements EncodedAlbumDao {

	@Override
	public EncodedAlbumBean findByArtistCodeAndCode(String artistCode, String code) {
			Query query = getSession().createQuery(
					"from encodedAlbumBean where code = :albumCode and encodedArtistBean.code = :artistCode");
			query.setString("albumCode", code);
			query.setString("artistCode", artistCode);
			return uniqueResult(query);
	}
	

	@Override
	public EncodedAlbumBean createExampleBean() {
		return new EncodedAlbumBean();
	}

	@Override
	public EncodedAlbumBean findByArtistAndFilename(
			EncodedArtistBean encodedArtistBean, String filename) {
		Query query = getSession().createQuery(
				"select al from encodedArtistBean ar join ar.encodedAlbumBeans al " +
				"where ar = :artist and al.filename = :filename").
			setEntity("artist", encodedArtistBean).
			setString("filename", filename);
		return uniqueResult(query);
	}

	@Override
	public SortedSet<EncodedAlbumBean> findAllEmptyAlbums() {
		Query query = getSession().createQuery(
				"from encodedAlbumBean a where a.encodedTrackBeans is empty");
		return asSortedSet(query);
	}
	
}
