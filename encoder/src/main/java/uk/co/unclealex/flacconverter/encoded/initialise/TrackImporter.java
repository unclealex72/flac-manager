package uk.co.unclealex.flacconverter.encoded.initialise;

import java.io.File;
import java.io.IOException;

import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;
import uk.co.unclealex.music.core.model.EncoderBean;

public interface TrackImporter {

	public void importTrack(EncoderBean encoderBean, File file, FlacTrackBean flacTrackBean) throws IOException;
}
