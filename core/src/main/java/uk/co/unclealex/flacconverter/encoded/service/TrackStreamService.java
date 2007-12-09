package uk.co.unclealex.flacconverter.encoded.service;

import java.io.InputStream;
import java.io.OutputStream;

import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;

public interface TrackStreamService {

	public InputStream getTrackInputStream(EncodedTrackBean encodedTrackBean);
	public OutputStream getTrackOutputStream(EncodedTrackBean encodedTrackBean);
	public int getMaximumTrackDataLength();	

}
