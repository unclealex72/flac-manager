/**
 * 
 */
package uk.co.unclealex.music.base.image;

import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * @author alex
 *
 */
public class Album {

	private List<String> i_artists;
	private String i_title;
	private String i_imageUrl;
	private int i_area;
	private List<String> i_tracks;
	
	/**
	 * 
	 */
	public Album() {
		super();
	}
	
	/**
	 * @param artist
	 * @param title
	 * @param imageUrl
	 * @param area
	 * @param tracks
	 */
	public Album(List<String> artist, String title, String imageUrl, int area, List<String> tracks) {
		i_artists = artist;
		i_title = title;
		i_imageUrl = imageUrl;
		i_area = area;
		i_tracks = tracks;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer("[");
		if (getArtists() != null) {
			buff.append(StringUtils.join(getArtists().toArray(), ", ")).append("; ");
		}
		buff.append(getTitle()).append("; ");
		buff.append(getImageUrl()).append(": ");
		if (getTracks() != null) {
			buff.append(StringUtils.join(getTracks().toArray(), ", "));
		}
		return buff.toString();
	}
	/**
	 * @return the artist
	 */
	public List<String> getArtists() {
		return i_artists;
	}

	/**
	 * @param artist the artist to set
	 */
	public void setArtists(List<String> artist) {
		i_artists = artist;
	}

	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return i_imageUrl;
	}

	/**
	 * @param imageUrl the imageUrl to set
	 */
	public void setImageUrl(String imageUrl) {
		i_imageUrl = imageUrl;
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
	 * @return the area
	 */
	public int getArea() {
		return i_area;
	}

	/**
	 * @param area the area to set
	 */
	public void setArea(int area) {
		i_area = area;
	}

	/**
	 * @return the tracks
	 */
	public List<String> getTracks() {
		return i_tracks;
	}

	/**
	 * @param tracks the tracks to set
	 */
	public void setTracks(List<String> tracks) {
		i_tracks = tracks;
	}
	
	
}
