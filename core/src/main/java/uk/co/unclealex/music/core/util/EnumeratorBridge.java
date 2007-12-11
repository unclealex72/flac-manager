package uk.co.unclealex.music.core.util;

import java.util.Enumeration;
import java.util.Iterator;

public class EnumeratorBridge<T> implements Enumeration<T> {

	private Iterator<T> i_iterator;

	public EnumeratorBridge(Iterator<T> iterator) {
		super();
		i_iterator = iterator;
	}

	@Override
	public boolean hasMoreElements() {
		return i_iterator.hasNext();
	}

	@Override
	public T nextElement() {
		return i_iterator.next();
	}
}
