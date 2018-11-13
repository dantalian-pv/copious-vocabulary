package ru.dantalian.copvoc.core;

public class CoreException extends Exception {

	private static final long serialVersionUID = 1L;

	private final boolean user;

	public CoreException() {
		this(false);
	}

	public CoreException(final boolean aUser) {
		user = aUser;
	}

	public CoreException(final String message, final Throwable cause) {
		this(message, cause, false);
	}

	public CoreException(final String message, final Throwable cause, final boolean aUser) {
		super(message, cause);
		user = aUser;
	}

	public CoreException(final String message) {
		this(message, false);
	}

	public CoreException(final String message, final boolean aUser) {
		super(message);
		user = aUser;
	}

	public boolean isUser() {
		return user;
	}

}
