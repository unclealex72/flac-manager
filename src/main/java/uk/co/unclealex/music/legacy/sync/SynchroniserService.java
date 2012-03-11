package uk.co.unclealex.music.legacy.sync;

import java.io.IOException;

public interface SynchroniserService {

	public void synchronise(String deviceName) throws IOException;

	public void synchroniseAll() throws IOException;
	
}
