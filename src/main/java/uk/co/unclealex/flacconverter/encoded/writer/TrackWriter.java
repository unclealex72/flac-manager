package uk.co.unclealex.flacconverter.encoded.writer;

import java.io.IOException;

import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;

public interface TrackWriter {

	public void create() throws IOException;
	
	public String write(EncodedTrackBean encodedTrackBean, String titleFormat) throws IOException;
	
	public void close() throws IOException;
}
