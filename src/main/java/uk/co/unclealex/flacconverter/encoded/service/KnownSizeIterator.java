package uk.co.unclealex.flacconverter.encoded.service;

import java.util.Iterator;

public class KnownSizeIterator<T> implements Iterator<T> {

	private int i_size;
	private Iterator<T> i_iterator;
	
	public KnownSizeIterator(int size, Iterator<T> iterator) {
		super();
		i_size = size;
		i_iterator = iterator;
	}

	public int size() {
		return i_size;
	}

	public boolean hasNext() {
		return i_iterator.hasNext();
	}

	public T next() {
		return i_iterator.next();
	}

	public void remove() {
		i_iterator.remove();
	}
}
