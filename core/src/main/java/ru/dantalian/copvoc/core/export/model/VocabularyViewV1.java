package ru.dantalian.copvoc.core.export.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VocabularyViewV1 {

	private String css;

	private String front;

	private String back;

	public VocabularyViewV1() {
	}

	public VocabularyViewV1(final String aCss, final String aFront, final String aBack) {
		css = aCss;
		front = aFront;
		back = aBack;
	}

	public String getCss() {
		return css;
	}

	public void setCss(final String aCss) {
		css = aCss;
	}

	public String getFront() {
		return front;
	}

	public void setFront(final String aFront) {
		front = aFront;
	}

	public String getBack() {
		return back;
	}

	public void setBack(final String aBack) {
		back = aBack;
	}

}
