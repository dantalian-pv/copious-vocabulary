package ru.dantalian.copvoc.suggester.api;

public class SuggestException extends Exception {

	private static final long serialVersionUID = 1L;

	public SuggestException() {
		super();
	}

	public SuggestException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public SuggestException(final String message) {
		super(message);
	}

}
