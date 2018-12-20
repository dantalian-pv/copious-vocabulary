package ru.dantalian.copvoc.web.controllers.rest.model;

public class DtoCardContent {

	private String name;

	private String text;

	public DtoCardContent() {
	}

	public DtoCardContent(final String aName, final String aText) {
		name = aName;
		text = aText;
	}

	public String getName() {
		return name;
	}

	public void setName(final String aName) {
		name = aName;
	}

	public String getText() {
		return text;
	}

	public void setText(final String aText) {
		text = aText;
	}

}
