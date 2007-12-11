package uk.co.unclealex.music.core.encoded.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.Iterator;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.io.SequenceOutputStream;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.util.EnumeratorBridge;

@Transactional
public class TrackStreamServiceImpl implements TrackStreamService {

	private	TrackDataStreamIteratorFactory i_trackDataStreamIteratorFactory;
	private int i_maximumTrackDataLength;

	private static final int BUFFER_STREAM_SIZE = 1024 * 1024;

	@Override
	public InputStream getTrackInputStream(EncodedTrackBean encodedTrackBean) {
		Iterator<InputStream> inIterator =
			getTrackDataStreamIteratorFactory().createTrackDataInputStreamIterator(encodedTrackBean);
		return
			new BufferedInputStream(
				new SequenceInputStream(new EnumeratorBridge<InputStream>(inIterator)),
				BUFFER_STREAM_SIZE);
	}
	
	@Override
	public OutputStream getTrackOutputStream(EncodedTrackBean encodedTrackBean) {
		Iterator<OutputStream> outIter = 
			getTrackDataStreamIteratorFactory().createTrackDataOutputStreamIterator(encodedTrackBean);
		return
			new BufferedOutputStream(
				new SequenceOutputStream(getMaximumTrackDataLength(), outIter),
				BUFFER_STREAM_SIZE);
	}

	public TrackDataStreamIteratorFactory getTrackDataStreamIteratorFactory() {
		return i_trackDataStreamIteratorFactory;
	}

	public void setTrackDataStreamIteratorFactory(
			TrackDataStreamIteratorFactory trackDataStreamIteratorFactory) {
		i_trackDataStreamIteratorFactory = trackDataStreamIteratorFactory;
	}

	public int getMaximumTrackDataLength() {
		return i_maximumTrackDataLength;
	}

	public void setMaximumTrackDataLength(int maximumTrackDataLength) {
		i_maximumTrackDataLength = maximumTrackDataLength;
	}

}
