package uk.co.unclealex.music.core.service;

import java.io.InputStream;
import java.io.OutputStream;

import uk.co.unclealex.music.core.model.EncodedTrackBean;

public interface TrackStreamService {

	public InputStream getTrackInputStream(EncodedTrackBean encodedTrackBean);
	public OutputStream getTrackOutputStream(EncodedTrackBean encodedTrackBean);
	public Integer getMaximumTrackDataLength();	

}
