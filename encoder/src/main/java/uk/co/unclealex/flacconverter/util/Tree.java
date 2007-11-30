package uk.co.unclealex.flacconverter.util;

import java.util.Iterator;
import java.util.Set;

public interface Tree<E> {

	public E getValue();
	
	public Tree<E> getParent();
	
	public Set<Tree<E>> getChildren();

	public Tree<E> findChild(E value);
	
	public Tree<E> traverse(Iterator<E> path);
	
	public Tree<E> createPath(Iterator<E> path);

	public void removeUpwards();
}
