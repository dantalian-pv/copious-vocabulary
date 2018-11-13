package ru.dantalian.copvac.persist.nitrite.model;

import javax.persistence.Id;

public class DbLanguage {

	@Id
	private String name;

	@Id
	private String country;

	@Id
	private String variant;

	private String text;

	public DbLanguage() {
	}

	public DbLanguage(final String aName, final String aCountry, final String aVariant, final String aText) {
		name = aName;
		country = aCountry;
		variant = aVariant;
		text = aText;
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

	@Override
	public String toString() {
		return "DbLanguage [name=" + name + ", country=" + country
				+ ", variant=" + variant + ", text=" + text + "]";
	}

}
