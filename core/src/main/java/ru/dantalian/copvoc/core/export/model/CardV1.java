package ru.dantalian.copvoc.core.export.model;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardV1 {

	private Map<String, String> content;

	public CardV1() {
	}

	public CardV1(final Map<String, String> aContent) {
		content = aContent;
	}

	public Map<String, String> getContent() {
		return content == null ? Collections.emptyMap() : content;
	}

	public void setContent(final Map<String, String> aContent) {
		content = aContent;
	}

}
