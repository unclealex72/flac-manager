/**
 * 
 */
package uk.co.unclealex.flacconverter;

import java.io.File;
import java.util.List;

/**
 * @author alex
 *
 */
public class FlacAlbum implements Comparable<FlacAlbum>{

	private String i_artist;
	private String i_title;
	private List<Track> i_tracks;
	
	/**
	 * 
	 */
	public FlacAlbum() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param artist
	 * @param title
	 * @param tracks
	 */
	public FlacAlbum(String artist, String title, List<Track> tracks) {
		super();
		i_artist = artist;
		i_title = title;
		i_tracks = tracks;
	}
	
	public File getDirectory() {
		if (i_tracks == null || i_tracks.size() == 0) {
			throw new IllegalStateException("This album has no tracks.");
		}
		return i_tracks.get(0).getFile().getParentFile();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(FlacAlbum o) {
		int cmp = getArtist().compareTo(o.getArtist());
		if (cmp != 0) {
			return cmp;
		}
		return getTitle().compareTo(o.getTitle());
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
	/**
	 * @return the tracks
	 */
	public List<Track> getTracks() {
		return i_tracks;
	}
	/**
	 * @param tracks the tracks to set
	 */
	public void setTracks(List<Track> tracks) {
		i_tracks = tracks;
	}
	
	
}
