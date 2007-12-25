package uk.co.unclealex.music.core.dao;

import java.util.SortedSet;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;

public interface EncodedAlbumDao extends KeyedDao<EncodedAlbumBean> {

	public EncodedAlbumBean findByArtistAndIdentifier(EncodedArtistBean encodedArtistBean, String albumIdentifier);
	
	public SortedSet<EncodedAlbumBean> findAllEmptyAlbums();

	public EncodedAlbumBean findByArtistAndFilename(
			EncodedArtistBean encodedArtistBean, String filename);

}
