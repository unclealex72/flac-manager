package uk.co.unclealex.music.encoder.dao;

import uk.co.unclealex.music.encoder.model.FlacAlbumBean;

public interface FlacAlbumDao extends CodeDao<FlacAlbumBean> {

	FlacAlbumBean findByArtistAndAlbum(String artistName, String albumName);

}
