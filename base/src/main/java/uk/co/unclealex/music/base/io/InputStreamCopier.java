package uk.co.unclealex.music.base.io;

import java.io.IOException;
import java.io.OutputStream;

public interface InputStreamCopier<E> {

	public void copy(DataExtractor<E> extractor, E element, KnownLengthOutputStream<?> out) throws IOException;

	public void copy(DataExtractor<E> extractor, E element, OutputStream out) throws IOException;

	public void copy(DataExtractor<E> extractor, int id, KnownLengthOutputStream<?> out) throws IOException;

	public void copy(DataExtractor<E> extractor, int id, OutputStream out) throws IOException;
}
