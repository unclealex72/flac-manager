package uk.co.unclealex.music.base.initialise;

import java.io.IOException;
import java.io.InputStream;

import uk.co.unclealex.music.base.model.EncodedAlbumBean;
import uk.co.unclealex.music.base.model.EncodedTrackBean;
import uk.co.unclealex.music.base.model.EncoderBean;

public interface TrackImporter {

	public EncodedTrackBean importTrack(
			InputStream in, int length, EncoderBean encoderBean, 
			String title, String url, int trackNumber, long lastModifiedMillis, EncodedAlbumBean encodedAlbumBean) throws IOException;	
}
