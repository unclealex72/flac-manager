package uk.co.unclealex.music.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class CanonicalAlbumCover {
	
	private String i_url;
	private long i_size;
	private String i_albumCode;
	private String i_artistCode;
	private boolean i_selected;
	private RawPictureData i_rawPictureData;
	
	public CanonicalAlbumCover(File file, long size, String artistCode, String albumCode, boolean selected) throws IOException {
		super();
		i_url = file.toURI().toURL().toString();
		byte[] rawPictureData = new byte[(int) file.length()]; 
		new FileInputStream(file).read(rawPictureData);
		i_rawPictureData = new RawPictureData(rawPictureData);
		i_size = size;
		i_albumCode = albumCode;
		i_artistCode = artistCode;
		i_selected = selected;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	public String getUrl() {
		return i_url;
	}

	public long getSize() {
		return i_size;
	}

	public String getAlbumCode() {
		return i_albumCode;
	}

	public String getArtistCode() {
		return i_artistCode;
	}

	public boolean isSelected() {
		return i_selected;
	}
	
	public RawPictureData getRawPictureData() {
		return i_rawPictureData;
	}
}
