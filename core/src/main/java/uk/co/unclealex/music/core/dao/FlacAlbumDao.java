package uk.co.unclealex.music.core.dao;

import uk.co.unclealex.music.core.model.FlacAlbumBean;

public interface FlacAlbumDao extends CodeDao<FlacAlbumBean> {

	FlacAlbumBean findByArtistAndAlbum(String artistName, String albumName);

}
