package uk.co.unclealex.flacconverter.encoded.service;

import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;

public interface TrackDataStreamIteratorFactory {

	public TrackDataInputStreamIterator createTrackDataInputStreamIterator(EncodedTrackBean encodedTrackBean);
	public TrackDataOutputStreamIterator createTrackDataOutputStreamIterator(EncodedTrackBean encodedTrackBean);
}
