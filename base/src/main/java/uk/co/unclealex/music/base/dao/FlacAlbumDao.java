package uk.co.unclealex.music.base.dao;

import uk.co.unclealex.music.base.model.FlacAlbumBean;

public interface FlacAlbumDao extends CodeDao<FlacAlbumBean> {

	public FlacAlbumBean findByArtistAndAlbum(String artistName, String albumName);
}
