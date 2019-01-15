package ru.dantalian.copvoc.web.controllers.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DtoValidationResult {

	@JsonProperty("valid")
	private boolean valid;

	@JsonProperty("message")
	private String message;

	public DtoValidationResult() {
	}

	public DtoValidationResult(final boolean aValid, final String aMessage) {
		super();
		valid = aValid;
		message = aMessage;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(final boolean aValid) {
		valid = aValid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String aMessage) {
		message = aMessage;
	}

	@Override
	public String toString() {
		return "DtoValidationResult [valid=" + valid + ", message=" + message + "]";
	}

}
