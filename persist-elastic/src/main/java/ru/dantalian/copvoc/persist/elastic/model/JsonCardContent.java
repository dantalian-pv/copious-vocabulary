package ru.dantalian.copvoc.persist.elastic.model;

public class JsonCardContent {

	private String fieldName;

	private String content;

	public JsonCardContent() {
	}

	public JsonCardContent(final String aFieldName, final String aContent) {
		fieldName = aFieldName;
		content = aContent;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(final String aFieldName) {
		fieldName = aFieldName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(final String aContent) {
		content = aContent;
	}

}
