package uk.co.unclealex.music.core.io;

import java.io.IOException;

public interface DataInjector<E> {

	public void injectData(E element, KnownLengthInputStream in) throws IOException;
}
