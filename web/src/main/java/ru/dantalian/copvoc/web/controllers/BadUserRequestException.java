package ru.dantalian.copvoc.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BadUserRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BadUserRequestException() {
		super();
	}

	public BadUserRequestException(final String aMessage, final Throwable aCause) {
		super(aMessage, aCause);
	}

	public BadUserRequestException(final String aMessage) {
		super(aMessage);
	}

}
