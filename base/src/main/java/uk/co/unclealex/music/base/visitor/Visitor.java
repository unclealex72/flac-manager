package uk.co.unclealex.music.base.visitor;

public abstract class Visitor<E extends Exception> {

	private E i_exception;

	public E getException() {
		return i_exception;
	}

	public void setException(E exception) {
		i_exception = exception;
	}
	
}
