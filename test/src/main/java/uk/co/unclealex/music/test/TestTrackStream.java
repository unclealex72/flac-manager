package uk.co.unclealex.music.test;

import java.util.Map;

import uk.co.unclealex.music.base.writer.TrackStream;

public interface TestTrackStream extends TrackStream {

	public Map<String, Integer> getFileNamesAndSizes();
}
