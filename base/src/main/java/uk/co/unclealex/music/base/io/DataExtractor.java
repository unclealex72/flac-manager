package uk.co.unclealex.music.base.io;

import java.io.IOException;

public interface DataExtractor<E> {

	public void extractData(E element, KnownLengthInputStreamCallback callback) throws IOException;

	public void extractData(int id, KnownLengthInputStreamCallback callback) throws IOException;
}
