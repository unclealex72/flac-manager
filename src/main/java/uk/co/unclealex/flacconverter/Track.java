/**
 * 
 */
package uk.co.unclealex.flacconverter;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * @author alex
 *
 */
public class Track implements Comparable<Track>{

	private File i_file;
	private String i_title;
	private int i_trackNumber;
	private Album i_album;
	private int i_year;
	private String i_genre = "";
	private long i_lastModified;
	
	private Track(File file) {
		i_file = file;
		if (file.exists()) {
			i_lastModified = file.lastModified();
		}
	}

	public Track(File file, Album album, String title, int trackNumber, int year, String genre)
	throws InvalidTrackException {
		this(file);
		checkNotNull("artist", album.getArtist());
		checkNotNull("album", album.getAlbum());
		i_album = album;
		i_title = checkNotNull("title", title);
		i_trackNumber = trackNumber;
		i_year = year;
		i_genre = checkNotNull("genre", genre);
	}

	private String checkNotNull(String name, String value) throws InvalidTrackException {
		if (value == null) {
			throw new InvalidTrackException("null " + name + " for track " + i_file.getAbsolutePath());
		}
		return value;
	}
	
	public String generateUniqueKey() {
		Album album = getAlbum();
		return album.getArtist() + getTrackNumber() + album.getAlbum();
	}
	
	public int compareTo(Track o) {
		int cmp = getAlbum().compareTo(o.getAlbum());
		if (cmp != 0) { return cmp; }
		cmp = new Integer(getTrackNumber()).compareTo(o.getTrackNumber());
		return cmp;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Track) && compareTo((Track) obj) == 0;
	}
	
	@Override
	public String toString() {
		Album album = getAlbum();
		return album.getArtist() + ", " + album.getAlbum() + ": " +
		getTrackNumber() + " - " + getTitle() + " (" + getYear() + ", " + getGenre() + ")";
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return i_file;
	}
	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		i_file = file;
	}
	/**
	 * @return the genre
	 */
	public String getGenre() {
		return i_genre;
	}
	/**
	 * @param genre the genre to set
	 */
	public void setGenre(String genre) {
		i_genre = genre;
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
	 * @return the trackNumber
	 */
	public int getTrackNumber() {
		return i_trackNumber;
	}
	/**
	 * @param trackNumber the trackNumber to set
	 */
	public void setTrackNumber(int trackNumber) {
		i_trackNumber = trackNumber;
	}
	/**
	 * @return the year
	 */
	public int getYear() {
		return i_year;
	}
	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		i_year = year;
	}
	
	/**
	 * @return the lastModified
	 */
	public long getLastModified() {
		return i_lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(long lastModified) {
		i_lastModified = lastModified;
	}
	
	public static Map<String, Track> makeMap(Collection<Track> tracks) {
		Map<String, Track> map = new HashMap<String, Track>();
		for (Track track : tracks) {
			map.put(track.generateUniqueKey(), track);
		}
		return map;
	}

	protected Album getAlbum() {
		return i_album;
	}

	protected void setAlbum(Album album) {
		i_album = album;
	}
}
