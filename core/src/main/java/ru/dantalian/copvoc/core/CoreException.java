package ru.dantalian.copvoc.core;

public class CoreException extends Exception {

	private static final long serialVersionUID = 1L;

	public CoreException() {
		super();
	}

	public CoreException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public CoreException(final String message) {
		super(message);
	}

}
