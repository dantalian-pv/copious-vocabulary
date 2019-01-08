package ru.dantalian.copvoc.web.controllers.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RestException extends ResponseStatusException {

	private static final long serialVersionUID = 1L;

	public RestException(final String aReason, final Throwable aCause) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, aReason, aCause);
	}

	public RestException(final String aReason) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, aReason);
	}

}
