package uk.co.unclealex.music.covers;

import java.net.URL;

public class ExternalCoverArtImage implements Comparable<ExternalCoverArtImage> {

	private URL i_url;
	private long i_size;
	
	public ExternalCoverArtImage(URL url, long size) {
		super();
		i_url = url;
		i_size = size;
	}

	@Override
	public String toString() {
		return getUrl().toString();
	}
	
	@Override
	public int compareTo(ExternalCoverArtImage o) {
		int cmp = Long.valueOf(getSize()).compareTo(Long.valueOf(o.getSize()));
		return cmp == 0?getUrl().toString().compareTo(o.getUrl().toString()):cmp;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof ExternalCoverArtImage && compareTo((ExternalCoverArtImage) obj) == 0;
	}
	
	@Override
	public int hashCode() {
		return getUrl().hashCode();
	}
	
	public URL getUrl() {
		return i_url;
	}
	
	public long getSize() {
		return i_size;
	}
}
