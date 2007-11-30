package uk.co.unclealex.flacconverter.encoded.service;

import java.io.InputStream;
import java.util.Iterator;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.flacconverter.encoded.model.EncodedTrackBean;

@Transactional
public interface TrackDataInputStreamIterator extends Iterator<InputStream> {

	public void initialise(EncodedTrackBean encodedTrackBean);
}
