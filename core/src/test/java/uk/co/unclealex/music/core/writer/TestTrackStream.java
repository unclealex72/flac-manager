package uk.co.unclealex.music.core.writer;

import java.util.Map;

import uk.co.unclealex.music.core.writer.TrackStream;

public interface TestTrackStream extends TrackStream {

	public Map<String, Integer> getFileNamesAndSizes();
}
