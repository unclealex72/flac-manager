package uk.co.unclealex.music.core.service.filesystem;

public abstract class ValueReturningPathInformationBeanVisitor<E> extends
		PathInformationBeanVisitor {

	private E i_value;

	public E getValue() {
		return i_value;
	}

	protected void setValue(E value) {
		i_value = value;
	}

}
