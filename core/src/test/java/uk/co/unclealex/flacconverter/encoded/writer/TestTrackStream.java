package uk.co.unclealex.flacconverter.encoded.writer;

import java.util.Map;

import uk.co.unclealex.music.core.encoded.writer.TrackStream;

public interface TestTrackStream extends TrackStream {

	public Map<String, Integer> getFileNamesAndSizes();
}
