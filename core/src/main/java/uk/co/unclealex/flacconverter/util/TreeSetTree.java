package uk.co.unclealex.flacconverter.util;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;

public class TreeSetTree<E extends Comparable<E>> implements Tree<E>, Comparable<Tree<E>> {

	private E i_value;
	private SortedSet<Tree<E>> i_children;
	private Tree<E> i_parent;
	
	public TreeSetTree(E value) {
		this(value, new TreeSet<Tree<E>>());
	}

	public TreeSetTree(E value, SortedSet<Tree<E>> children) {
		super();
		i_value = value;
		i_children = children;
	}

	protected Predicate<Tree<E>> createChildPredicate(final E value) {
		return new Predicate<Tree<E>>() {
			@Override
			public boolean evaluate(Tree<E> tree) {
				return value.equals(tree.getValue());
			}
		};
	}
		
	@Override
	public Tree<E> findChild(E value) {
		return CollectionUtils.find(getChildren(), createChildPredicate(value));
	}
	
	public Tree<E> traverse(Iterator<E> path) {
		if (!path.hasNext()) {
			return this;
		}
		Tree<E> child = findChild(path.next());
		return child==null?null:child.traverse(path);
	}

	@Override
	public Tree<E> createPath(Iterator<E> path) {
		if (!path.hasNext()) {
			return this;
		}
		E value = path.next();
		Tree<E> child = findChild(value);
		if (child == null) {
			TreeSetTree<E> newChild = new TreeSetTree<E>(value);
			newChild.setParent(this);
			getChildren().add(newChild);
			child = newChild;
		}
		return child.createPath(path);
	}
	
	@Override
	public void removeUpwards() {
		Tree<E> parent = getParent();
		if (parent != null) {
			Set<Tree<E>> siblings = parent.getChildren();
			siblings.remove(this);
			if (siblings.isEmpty()) {
				parent.removeUpwards();
			}
		}
	}
	@Override
	public int hashCode() {
		E value = getValue();
		return value == null?0:value.hashCode();
	}
	
	@Override
	public String toString() {
		return "{" + getValue() + ":" + getChildren().toString();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Tree)) {
			return false;
		}
		Tree<E> other  = (Tree<E>) obj;
		return getValue().equals(other.getValue()) && getChildren().equals(other.getChildren());
	}
	
	@Override
	public int compareTo(Tree<E> o) {
		return getValue().compareTo(o.getValue());
	}
	
	public E getValue() {
		return i_value;
	}

	protected void setValue(E value) {
		i_value = value;
	}

	public SortedSet<Tree<E>> getChildren() {
		return i_children;
	}

	protected void setChildren(SortedSet<Tree<E>> children) {
		i_children = children;
	}

	public Tree<E> getParent() {
		return i_parent;
	}

	public void setParent(Tree<E> parent) {
		i_parent = parent;
	}
}
