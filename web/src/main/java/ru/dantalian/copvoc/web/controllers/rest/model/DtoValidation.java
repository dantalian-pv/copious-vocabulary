package ru.dantalian.copvoc.web.controllers.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DtoValidation {

	@JsonProperty("answer")
	private String answer;

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(final String aAnswer) {
		answer = aAnswer;
	}

}
