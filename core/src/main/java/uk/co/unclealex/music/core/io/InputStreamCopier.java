package uk.co.unclealex.music.core.io;

import java.io.IOException;
import java.io.OutputStream;

public interface InputStreamCopier {

	public void copy(DataExtractor extractor, int id, KnownLengthOutputStream<?> out) throws IOException;

	public void copy(DataExtractor extractor, int id, OutputStream out) throws IOException;
}
