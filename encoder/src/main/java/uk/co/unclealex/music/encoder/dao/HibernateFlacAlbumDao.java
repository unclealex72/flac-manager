package uk.co.unclealex.music.encoder.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import uk.co.unclealex.music.encoder.model.FlacAlbumBean;
import uk.co.unclealex.music.encoder.model.FlacArtistBean;

@Repository
public class HibernateFlacAlbumDao extends HibernateCodeDao<FlacAlbumBean> implements
		FlacAlbumDao {

	@Autowired
	public HibernateFlacAlbumDao(@Qualifier("flacSessionFactory") SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public FlacAlbumBean findByArtistAndAlbum(String artistName, String albumName) {
		FlacAlbumBean example = createExampleBean();
		example.setCode(albumName);
		FlacArtistBean flacArtistBean = new FlacArtistBean();
		flacArtistBean.setCode(artistName);
		example.setFlacArtistBean(flacArtistBean);
		return findByExample(example);
	}

	@Override
	public FlacAlbumBean createExampleBean() {
		return new FlacAlbumBean();
	}

}
