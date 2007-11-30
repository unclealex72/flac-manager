package uk.co.unclealex.flacconverter.encoded.initialise;

import java.io.File;
import java.io.IOException;

import uk.co.unclealex.flacconverter.encoded.model.EncoderBean;
import uk.co.unclealex.flacconverter.flac.model.FlacTrackBean;

public interface TrackImporter {

	public void importTrack(EncoderBean encoderBean, File file, FlacTrackBean flacTrackBean) throws IOException;
}
