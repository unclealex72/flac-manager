/**
 * 
 */
package uk.co.unclealex.flacconverter;

import java.util.Iterator;

/**
 * @author alex
 *
 */
public class IterableIterator<E> implements Iterable<E>, Iterator<E> {

	private Iterator<E> i_iterator;

	/**
	 * @param iterator
	 */
	public IterableIterator(Iterator<E> iterator) {
		super();
		i_iterator = iterator;
	}

	/**
	 * @return
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return i_iterator.hasNext();
	}

	/**
	 * @return
	 * @see java.util.Iterator#next()
	 */
	public E next() {
		return i_iterator.next();
	}

	/**
	 * 
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		i_iterator.remove();
	}

	public Iterator<E> iterator() {
		return i_iterator;
	}

}
