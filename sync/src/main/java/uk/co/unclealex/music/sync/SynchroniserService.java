package uk.co.unclealex.music.sync;

import java.io.IOException;

public interface SynchroniserService {

	public void synchronise(String deviceName) throws IOException;
	
}
