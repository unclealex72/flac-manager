package uk.co.unclealex.music.core.service.filesystem;

public class PathNotFoundException extends Exception {

	public PathNotFoundException() {
	}

	public PathNotFoundException(String message) {
		super(message);
	}

	public PathNotFoundException(Throwable cause) {
		super(cause);
	}

	public PathNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
