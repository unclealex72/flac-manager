package uk.co.unclealex.music.encoder.flac.dao;

import uk.co.unclealex.music.encoder.flac.model.FlacAlbumBean;

public interface FlacAlbumDao extends CodeDao<FlacAlbumBean> {

	FlacAlbumBean findByArtistAndAlbum(String artistName, String albumName);

}
