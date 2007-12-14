package uk.co.unclealex.music.core.service;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedArtistBean;

public interface EncodedService {

	public EncodedArtistBean findOrCreateArtist(String identifier, String name);
	public EncodedAlbumBean findOrCreateAlbum(EncodedArtistBean encodedArtistBean, String identifier, String title);
}
