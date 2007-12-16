package uk.co.unclealex.music.encoder.encoded.service;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.encoder.model.FlacAlbumBean;

public interface FlacTrackService {

	public EncodedAlbumBean findOrCreateEncodedAlbumBean(FlacAlbumBean flacAlbumBean);
}
