package uk.co.unclealex.music.core.encoded.service;

import java.io.InputStream;
import java.io.OutputStream;

import uk.co.unclealex.music.core.encoded.model.EncodedTrackBean;

public interface TrackStreamService {

	public InputStream getTrackInputStream(EncodedTrackBean encodedTrackBean);
	public OutputStream getTrackOutputStream(EncodedTrackBean encodedTrackBean);
	public int getMaximumTrackDataLength();	

}