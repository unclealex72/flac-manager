package uk.co.unclealex.flacconverter.encoded.writer;

import java.util.Map;

public interface TestTrackStream extends TrackStream {

	public Map<String, Integer> getFileNamesAndSizes();
}
