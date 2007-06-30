package uk.co.unclealex.flacconverter;

public class Album implements Comparable<Album> {

	private String i_artist;
	private String i_album;
	
	public Album() {
		super();
	}
	
	public Album(String artist, String album) {
		super();
		i_artist = artist;
		i_album = album;
	}

	public int compareTo(Album o) {
		int cmp = getArtist().compareTo(o.getArtist());
		if (cmp != 0) { return cmp; }
		return getAlbum().compareTo(o.getAlbum());
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Album && compareTo((Album) obj) == 0;
	}
	
	@Override
	public int hashCode() {
		String album = getAlbum();
		String artist = getArtist();
		return (album==null?0:album.hashCode()) + (artist==null?0:artist.hashCode());
	}
	
	@Override
	public String toString() {
		return getArtist() + ", " + getAlbum();
	}
	
	protected String getAlbum() {
		return i_album;
	}
	protected void setAlbum(String album) {
		i_album = album;
	}
	protected String getArtist() {
		return i_artist;
	}
	protected void setArtist(String artist) {
		i_artist = artist;
	}
}
