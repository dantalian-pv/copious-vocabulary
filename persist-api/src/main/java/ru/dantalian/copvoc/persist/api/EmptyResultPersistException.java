package ru.dantalian.copvoc.persist.api;

public class EmptyResultPersistException extends PersistException {

	private static final long serialVersionUID = 1L;

	public EmptyResultPersistException() {
		super();
	}

	public EmptyResultPersistException(final String aMessage, final Throwable aCause) {
		super(aMessage, aCause);
	}

	public EmptyResultPersistException(final String aMessage) {
		super(aMessage);
	}

}
