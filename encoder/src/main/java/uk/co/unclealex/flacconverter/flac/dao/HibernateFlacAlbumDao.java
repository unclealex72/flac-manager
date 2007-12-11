package uk.co.unclealex.flacconverter.flac.dao;

import uk.co.unclealex.music.encoder.flac.model.FlacAlbumBean;
import uk.co.unclealex.music.encoder.flac.model.FlacArtistBean;

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
