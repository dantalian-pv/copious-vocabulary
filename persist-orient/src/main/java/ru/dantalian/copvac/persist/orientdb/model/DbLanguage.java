package ru.dantalian.copvac.persist.orientdb.model;

import javax.persistence.EmbeddedId;

public class DbLanguage {

	@EmbeddedId
	private DbLanguageId id;

	private String text;

	private String variant;

	public DbLanguage() {
	}

	public DbLanguage(final String aName, final String aCountry, final String aVariant, final String aText) {
		id = new DbLanguageId(aName, aCountry);
		variant = aVariant;
		text = aText;
	}

	public DbLanguageId getId() {
		return id;
	}

	public void setId(final DbLanguageId aId) {
		id = aId;
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
		return "DbLanguage [id=" + id + ", variant=" + variant + ", text=" + text + "]";
	}

}
