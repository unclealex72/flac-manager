package uk.co.unclealex.music.core.encoded.service;

import uk.co.unclealex.music.core.encoded.model.EncodedTrackBean;

public interface TrackDataStreamIteratorFactory {

	public TrackDataInputStreamIterator createTrackDataInputStreamIterator(EncodedTrackBean encodedTrackBean);
	public TrackDataOutputStreamIterator createTrackDataOutputStreamIterator(EncodedTrackBean encodedTrackBean);
}
