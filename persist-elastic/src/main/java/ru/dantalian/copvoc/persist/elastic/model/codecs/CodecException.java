package ru.dantalian.copvoc.persist.elastic.model.codecs;

public class CodecException extends Exception {

	private static final long serialVersionUID = 1L;

	public CodecException() {
		super();
	}

	public CodecException(final String aMessage) {
		super(aMessage);
	}

	public CodecException(final String aMessage, final Throwable aCause) {
		super(aMessage, aCause);
	}

}
