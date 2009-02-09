/**
 * 
 */
package uk.co.unclealex.music.core.image;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import uk.co.unclealex.music.base.image.Album;

/**
 * @author alex
 *
 */
public class AlbumComparator implements Comparator<Album> {

	private String i_artist;
	private String i_title;
	
	/**
	 * 
	 */
	public AlbumComparator() {
		super();
	}

	/**
	 * @param artist
	 * @param title
	 */
	public AlbumComparator(String artist, String title) {
		super();
		i_artist = artist;
		i_title = title;
	}

	public int compare(Album o1, Album o2) {
		String artist = getArtist();
		String title = getTitle();
		List<String> artists1 = o1.getArtists();
		List<String> artists2 = o2.getArtists();
		boolean empty1 = isEmpty(artists1);
		boolean empty2 = isEmpty(artists2);
		if (empty1 && !empty2) {
			return 1;
		}
		if (!empty1 && empty2) {
			return -1;
		}
		if (!empty1 && !empty2) {
			int diff1 = StringUtils.getLevenshteinDistance(artist, artists1.get(0));
			int diff2 = StringUtils.getLevenshteinDistance(artist, artists2.get(0));
			int cmp = new Integer(diff1).compareTo(diff2);
			if (cmp != 0) {
				return cmp;
			}
		}
		int diff1 = StringUtils.getLevenshteinDistance(title, o1.getTitle());
		int diff2 = StringUtils.getLevenshteinDistance(title, o2.getTitle());
		int cmp = new Integer(diff1).compareTo(diff2);
		if (cmp != 0) {
			return cmp;
		}
		
		return new Integer(o2.getArea()).compareTo(o1.getArea());
	}

	private boolean isEmpty(Collection<String> collection) {
		return collection == null || collection.size() == 0;
	}
	/**
	 * @return the artist
	 */
	public String getArtist() {
		return i_artist;
	}

	/**
	 * @param artist the artist to set
	 */
	public void setArtist(String artist) {
		i_artist = artist;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return i_title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		i_title = title;
	}

}
