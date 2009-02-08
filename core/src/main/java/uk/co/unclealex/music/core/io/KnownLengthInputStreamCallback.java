package uk.co.unclealex.music.core.io;

import java.io.IOException;

public interface KnownLengthInputStreamCallback {

	public void execute(KnownLengthInputStream in) throws IOException;
}
