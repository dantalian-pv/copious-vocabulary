package ru.dantalian.copvoc.web.controllers.rest.model;

public class DtoVoid {

	public static final DtoVoid INSTANCE = new DtoVoid(true);

	private boolean result;

	public DtoVoid() {
	}

	public DtoVoid(final boolean aResult) {
		result = aResult;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(final boolean aResult) {
		result = aResult;
	}

}
