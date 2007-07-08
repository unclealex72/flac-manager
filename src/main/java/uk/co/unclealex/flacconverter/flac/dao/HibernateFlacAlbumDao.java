package uk.co.unclealex.flacconverter.flac.dao;

import uk.co.unclealex.flacconverter.flac.model.FlacAlbumBean;
import uk.co.unclealex.flacconverter.flac.model.FlacArtistBean;

public class HibernateFlacAlbumDao extends HibernateCodeDao<FlacAlbumBean> implements
		FlacAlbumDao {

	@Override
	public FlacAlbumBean findByArtistAndAlbum(String artistName, String albumName) {
		FlacAlbumBean example = createBlankExample();
		example.setCode(albumName);
		FlacArtistBean flacArtistBean = new FlacArtistBean();
		flacArtistBean.setCode(artistName);
		example.setFlacArtistBean(flacArtistBean);
		return findByExample(example);
	}

	@Override
	protected FlacAlbumBean createBlankExample() {
		return new FlacAlbumBean();
	}

}
