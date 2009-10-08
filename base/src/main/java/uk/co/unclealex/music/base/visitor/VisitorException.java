package uk.co.unclealex.music.base.visitor;

public class VisitorException extends RuntimeException {

	public VisitorException(Exception cause) {
		super(cause);
	}


	@SuppressWarnings("unchecked")
	public <E extends Exception> E getTypedCause() {
		return (E) getCause();
	}
}
