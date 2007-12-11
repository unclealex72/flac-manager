package uk.co.unclealex.music.core.service;

import uk.co.unclealex.music.core.model.EncodedTrackBean;

public interface TrackDataStreamIteratorFactory {

	public TrackDataInputStreamIterator createTrackDataInputStreamIterator(EncodedTrackBean encodedTrackBean);
	public TrackDataOutputStreamIterator createTrackDataOutputStreamIterator(EncodedTrackBean encodedTrackBean);
}
