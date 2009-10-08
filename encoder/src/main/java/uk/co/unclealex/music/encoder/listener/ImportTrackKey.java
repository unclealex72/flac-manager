package uk.co.unclealex.music.encoder.listener;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ImportTrackKey implements Comparable<ImportTrackKey> {

	private String i_artist;
	private String i_album;
	private int i_trackNumber;
	private String i_title;
	private String i_extension;
	
	public ImportTrackKey(String artist, String album, int trackNumber, String title, String extension) {
		super();
		i_artist = artist;
		i_album = album;
		i_trackNumber = trackNumber;
		i_title = title;
		i_extension = extension;
	}

	@Override
	public String toString() {
		return getArtist() + ", " + getAlbum() + ": " + getTrackNumber() + " " + getTitle() + "." + getExtension();
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof ImportTrackKey) && EqualsBuilder.reflectionEquals(this, obj);
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public int compareTo(ImportTrackKey o) {
		int cmp = getArtist().compareTo(o.getArtist());
		if (cmp == 0) {
			cmp = getAlbum().compareTo(o.getAlbum());
		}
		if (cmp == 0) {
			cmp = getTrackNumber() - o.getTrackNumber();
		}
		if (cmp == 0) {
			cmp = getTitle().compareTo(o.getTitle());
		}
		if (cmp == 0) {
			cmp = getExtension().compareTo(o.getExtension());
		}
		return cmp;
	}
	
	public String getArtist() {
		return i_artist;
	}
	
	public String getAlbum() {
		return i_album;
	}

	public int getTrackNumber() {
		return i_trackNumber;
	}
	
	public String getTitle() {
		return i_title;
	}
	
	public String getExtension() {
		return i_extension;
	}
}
