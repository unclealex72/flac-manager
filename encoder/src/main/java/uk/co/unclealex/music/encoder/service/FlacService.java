package uk.co.unclealex.music.encoder.service;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.encoder.model.FlacAlbumBean;

public interface FlacService {

	public EncodedAlbumBean findOrCreateEncodedAlbumBean(FlacAlbumBean flacAlbumBean);
	
	public String getRootUrl();
	
}
