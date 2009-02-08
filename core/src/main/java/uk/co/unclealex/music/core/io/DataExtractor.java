package uk.co.unclealex.music.core.io;

import java.io.IOException;

public interface DataExtractor {

	public void extractData(int id, KnownLengthInputStreamCallback callback) throws IOException;
}
