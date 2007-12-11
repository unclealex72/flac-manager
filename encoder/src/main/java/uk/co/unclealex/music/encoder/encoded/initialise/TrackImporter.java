package uk.co.unclealex.music.encoder.encoded.initialise;

import java.io.File;
import java.io.IOException;

import uk.co.unclealex.music.core.model.EncoderBean;
import uk.co.unclealex.music.encoder.flac.model.FlacTrackBean;

public interface TrackImporter {

	public void importTrack(EncoderBean encoderBean, File file, FlacTrackBean flacTrackBean) throws IOException;
}
