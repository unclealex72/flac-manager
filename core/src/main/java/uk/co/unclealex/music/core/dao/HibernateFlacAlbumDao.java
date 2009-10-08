package uk.co.unclealex.music.core.dao;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.base.dao.FlacAlbumDao;
import uk.co.unclealex.music.base.model.FlacAlbumBean;
import uk.co.unclealex.music.base.model.FlacArtistBean;

@Transactional
public class HibernateFlacAlbumDao extends HibernateCodeDao<FlacAlbumBean> implements
		FlacAlbumDao {

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
