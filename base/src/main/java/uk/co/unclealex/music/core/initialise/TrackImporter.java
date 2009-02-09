package uk.co.unclealex.music.core.initialise;

import java.io.IOException;
import java.io.InputStream;

import uk.co.unclealex.music.core.model.EncodedAlbumBean;
import uk.co.unclealex.music.core.model.EncodedTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;

public interface TrackImporter {

	public EncodedTrackBean importTrack(
			InputStream in, int length, EncoderBean encoderBean, 
			String title, String url, int trackNumber, long lastModifiedMillis, EncodedAlbumBean encodedAlbumBean) throws IOException;	
}