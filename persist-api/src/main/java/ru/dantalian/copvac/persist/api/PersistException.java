package ru.dantalian.copvac.persist.api;

public class PersistException extends Exception {

	private static final long serialVersionUID = 1L;

	public PersistException() {
		super();
	}

	public PersistException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public PersistException(final String message) {
		super(message);
	}

}
