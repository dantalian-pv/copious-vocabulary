package ru.dantalian.copvoc.web.controllers.rest;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RestExceptionBody {

	@JsonProperty("status")
	protected int status;

	@JsonProperty("message")
	protected String message;

	public RestExceptionBody() {
	}

	public RestExceptionBody(final HttpStatus aStatus, final String aMessage) {
		this(aStatus.value(), aMessage);
	}

	public RestExceptionBody(final int aStatus, final String aMessage) {
		status = aStatus;
		message = aMessage;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(final int aStatus) {
		status = aStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String aMessage) {
		message = aMessage;
	}

}
