package uk.co.unclealex.music.core.encoded.service;

import java.io.InputStream;
import java.util.Iterator;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.music.core.encoded.model.EncodedTrackBean;

@Transactional
public interface TrackDataInputStreamIterator extends Iterator<InputStream> {

	public void initialise(EncodedTrackBean encodedTrackBean);
}
