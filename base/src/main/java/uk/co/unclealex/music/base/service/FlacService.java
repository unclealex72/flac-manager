package uk.co.unclealex.music.base.service;

import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.FlacAlbumBean;

public interface FlacService {

	public EncodedAlbumBean findOrCreateEncodedAlbumBean(FlacAlbumBean flacAlbumBean);
	
	public String getPathForFlacAlbum(FlacAlbumBean flacAlbumBean);
	public FlacAlbumBean findFlacAlbumByPath(String path);
	
	public String getRootUrl();
	
}
