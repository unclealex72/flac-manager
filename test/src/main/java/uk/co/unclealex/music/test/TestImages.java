/**
 * 
 */
package uk.co.unclealex.music.test;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import uk.co.unclealex.music.base.image.Album;
import uk.co.unclealex.music.base.image.SearchManager;
import uk.co.unclealex.music.base.image.AlbumComparator;

/**
 * @author alex
 *
 */
public class TestImages {

	private static final String ARTIST = "Puccini";
	private static final String TITLE = "Madama Butterfly";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SearchManager manager = new TestAmazonSearchManager();
		Collection<Album> albums = manager.search(ARTIST, TITLE);
		SortedSet<Album> sortedAlbums = new TreeSet<Album>(new AlbumComparator(ARTIST, TITLE));
		sortedAlbums.addAll(albums);
		for (Album album : sortedAlbums) {
			List<String> artists = album.getArtists();
			String artist = artists.isEmpty()?"<no-one>":artists.get(0);
			System.out.println(artist + ", " + album.getTitle() + " (" + album.getArea() + ") " + album.getImageUrl());
		}
	}

}
