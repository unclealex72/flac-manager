package uk.co.unclealex.music.base.io;

import java.io.IOException;

public interface DataExtractor {

	public void extractData(int id, KnownLengthInputStreamCallback callback) throws IOException;
}
