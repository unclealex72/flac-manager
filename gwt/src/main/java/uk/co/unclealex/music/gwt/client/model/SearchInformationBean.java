package uk.co.unclealex.music.gwt.client.model;

import java.io.Serializable;

public class SearchInformationBean implements Serializable {

	private AlbumInformationBean i_albumInformationBean;
	private String i_artist;
	private String i_album;
	
	public SearchInformationBean() {
		super();
	}

	public SearchInformationBean(AlbumInformationBean albumInformationBean, String artist, String album) {
		super();
		i_albumInformationBean = albumInformationBean;
		i_artist = artist;
		i_album = album;
	}

	public AlbumInformationBean getAlbumInformationBean() {
		return i_albumInformationBean;
	}

	public String getArtist() {
		return i_artist;
	}

	public String getAlbum() {
		return i_album;
	}
}
