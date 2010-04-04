package uk.co.unclealex.music.encoder.listener;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ImportTrackKey {

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
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
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
