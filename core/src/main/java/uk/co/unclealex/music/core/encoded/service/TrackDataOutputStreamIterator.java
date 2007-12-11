package uk.co.unclealex.music.core.encoded.service;

import java.io.OutputStream;
import java.util.Iterator;

import uk.co.unclealex.music.core.model.EncodedTrackBean;

public interface TrackDataOutputStreamIterator extends Iterator<OutputStream> {

	public void initialise(EncodedTrackBean encodedTrackBean);
}
