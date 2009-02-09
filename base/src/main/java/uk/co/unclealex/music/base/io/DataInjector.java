package uk.co.unclealex.music.base.io;

import java.io.IOException;

public interface DataInjector<E> {

	public void injectData(E element, KnownLengthInputStream in) throws IOException;
}
