package uk.co.unclealex.music.encoder.service;

import java.util.Set;

import uk.co.unclealex.music.base.model.FlacAlbumBean;

public class EncoderResultBean {

	private Set<FlacAlbumBean> i_flacAlbumBeans;
	private int i_tracksAffected;
	
	public EncoderResultBean(Set<FlacAlbumBean> flacAlbumBeans, int tracksAffected) {
		super();
		i_flacAlbumBeans = flacAlbumBeans;
		i_tracksAffected = tracksAffected;
	}

	public Set<FlacAlbumBean> getFlacAlbumBeans() {
		return i_flacAlbumBeans;
	}
	
	public int getTracksAffected() {
		return i_tracksAffected;
	}
}
