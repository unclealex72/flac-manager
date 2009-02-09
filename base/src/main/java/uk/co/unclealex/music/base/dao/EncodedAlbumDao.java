package uk.co.unclealex.music.base.dao;

import java.util.SortedSet;

import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;

public interface EncodedAlbumDao extends KeyedDao<EncodedAlbumBean> {

	public EncodedAlbumBean findByArtistAndIdentifier(EncodedArtistBean encodedArtistBean, String albumIdentifier);
	
	public SortedSet<EncodedAlbumBean> findAllEmptyAlbums();

	public EncodedAlbumBean findByArtistAndFilename(
			EncodedArtistBean encodedArtistBean, String filename);

}
