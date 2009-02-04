package uk.co.unclealex.music.core.service;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.FlacAlbumBean;

public interface FlacService {

	public EncodedAlbumBean findOrCreateEncodedAlbumBean(FlacAlbumBean flacAlbumBean);
	
	public String getPathForFlacAlbum(FlacAlbumBean flacAlbumBean);
	public FlacAlbumBean findFlacAlbumByPath(String path);
	
	public String getRootUrl();
	
}
