package ru.dantalian.copvoc.web.controllers.rest.model;

public class DtoLanguage {

	private String id;

	private String name;

	private String country;

	private String variant;

	private String text;

	public DtoLanguage() {
	}

	public DtoLanguage(final String aId, final String aName, final String aCountry,
			final String aVariant, final String aText) {
		id = aId;
		name = aName;
		country = aCountry;
		variant = aVariant;
		text = aText;
	}

	public String getId() {
		return id;
	}

	public void setId(final String aId) {
		id = aId;
	}

	public String getName() {
		return name;
	}

	public void setName(final String aName) {
		name = aName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(final String aCountry) {
		country = aCountry;
	}

	public String getVariant() {
		return variant;
	}

	public void setVariant(final String aVariant) {
		variant = aVariant;
	}

	public String getText() {
		return text;
	}

	public void setText(final String aText) {
		text = aText;
	}

}
