package uk.co.unclealex.flacconverter.encoded.service;

import java.io.OutputStream;
import java.util.Iterator;

import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;

public interface TrackDataOutputStreamIterator extends Iterator<OutputStream> {

	public void initialise(EncodedTrackBean encodedTrackBean);
}
