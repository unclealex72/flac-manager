package uk.co.unclealex.music.core.dao;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;

public interface EncodedAlbumDao extends KeyedDao<EncodedAlbumBean> {

	public EncodedAlbumBean findByArtistAndTitle(String artistName, String albumTitle);
}
