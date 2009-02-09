package uk.co.unclealex.music.base.io;

import java.io.IOException;

public interface KnownLengthInputStreamCallback {

	public void execute(KnownLengthInputStream in) throws IOException;
}
