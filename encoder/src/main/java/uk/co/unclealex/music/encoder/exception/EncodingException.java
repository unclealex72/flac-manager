package uk.co.unclealex.music.encoder.exception;

public abstract class EncodingException extends Exception {

	public EncodingException() {
		super();
	}

	public EncodingException(String message, Throwable cause) {
		super(message, cause);
	}

	public EncodingException(String message) {
		super(message);
	}

	public EncodingException(Throwable cause) {
		super(cause);
	}
	
}
