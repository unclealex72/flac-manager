package uk.co.unclealex.music.legacy;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


public abstract class AbstractDevice implements Device {

	private final String i_name;
	private final String i_owner;
	private final Encoding i_encoding;
	private final boolean i_playlistsSupported;
	
	public AbstractDevice(String name, String owner, Encoding encoding, boolean playlistsSupported) {
		super();
		i_name = name;
		i_owner = owner;
		i_encoding = encoding;
		i_playlistsSupported = playlistsSupported;
	}

	@Override
	public boolean arePlaylistsSupported() {
		return isPlaylistsSupported();
	}

	@Override
	public int compareTo(Device o) {
		return toString().compareTo(o.toString());
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	@Override
	public String toString() {
		return String.format("%s's %s (%s)", getOwner(), getName(), getEncoding().getExtension());
	}
	
	public String getName() {
		return i_name;
	}
	
	public String getOwner() {
		return i_owner;
	}
	
	public Encoding getEncoding() {
		return i_encoding;
	}

	public boolean isPlaylistsSupported() {
		return i_playlistsSupported;
	}
	
}
