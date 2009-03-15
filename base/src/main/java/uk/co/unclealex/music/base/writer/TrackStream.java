package uk.co.unclealex.music.base.writer;

import java.io.IOException;

import uk.co.unclealex.music.base.io.KnownLengthOutputStream;
import uk.co.unclealex.music.base.model.EncodedTrackBean;

public interface TrackStream {

	public void create() throws IOException;
	
	public KnownLengthOutputStream createStream(EncodedTrackBean encodedTrackBean, String title) throws IOException;
	
	public void closeStream() throws IOException;
	
	public void close() throws IOException;
}
