package uk.co.unclealex.music.base.writer;

import java.io.IOException;
import java.io.OutputStream;

import uk.co.unclealex.music.base.model.EncodedTrackBean;

public interface TrackStream {

	public void create() throws IOException;
	
	public OutputStream createStream(EncodedTrackBean encodedTrackBean, String title) throws IOException;
	
	public void closeStream() throws IOException;
	
	public void close() throws IOException;
}
