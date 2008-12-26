package uk.co.unclealex.music.albumcover.model;

import java.net.URL;

import org.apache.commons.lang.ObjectUtils;

public class AlbumCoverBean {

	private URL i_url;
	private AlbumCoverSize i_albumCoverSize;
	
	public AlbumCoverBean(URL url, AlbumCoverSize albumCoverSize) {
		super();
		i_url = url;
		i_albumCoverSize = albumCoverSize;
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(getUrl()) + 3 * ObjectUtils.hashCode(getAlbumCoverSize());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AlbumCoverBean)) {
			return false;
		}
		AlbumCoverBean other = (AlbumCoverBean) obj;
		return ObjectUtils.equals(getUrl(), other.getUrl()) && ObjectUtils.equals(getAlbumCoverSize(), other.getAlbumCoverSize());
	}
	
	@Override
	public String toString() {
		return getUrl() + "(" + getAlbumCoverSize() + ")";
	}
	
	public URL getUrl() {
		return i_url;
	}
	
	public AlbumCoverSize getAlbumCoverSize() {
		return i_albumCoverSize;
	}
}
