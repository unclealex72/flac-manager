package uk.co.unclealex.music.base.service;

import java.util.SortedSet;

import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedArtistBean;
import uk.co.unclealex.music.base.model.EncodedBean;

public interface EncodedService {

	public EncodedArtistBean findOrCreateArtist(String identifier, String name);
	
	public EncodedAlbumBean findOrCreateAlbum(EncodedArtistBean encodedArtistBean, String identifier, String title);
	
	public SortedSet<Character> getAllFirstLettersOfArtists();
	/**
	 * Remove any empty albums and artists.
	 * @return The number of albums removed.
	 */
	public int removeEmptyAlbumsAndArtists();
	
	public void updateAllFilenames();
	
	public void injectFilename(EncodedBean encodedBean);
}
