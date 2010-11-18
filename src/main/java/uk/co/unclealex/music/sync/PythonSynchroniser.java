package uk.co.unclealex.music.sync;

import java.io.IOException;
import java.io.InputStream;

import uk.co.unclealex.music.Device;

public abstract class PythonSynchroniser<D extends Device> extends ProcessSynchroniser<D> {

	@Override
	protected InputStream getCommandAsStream() throws IOException {
		return getClass().getResourceAsStream("sync.py");
	}
	
}