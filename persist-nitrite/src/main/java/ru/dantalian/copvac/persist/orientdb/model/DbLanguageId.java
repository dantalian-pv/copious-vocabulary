package ru.dantalian.copvac.persist.orientdb.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class DbLanguageId implements Serializable {

	private String name;

	private String country;

	public DbLanguageId() {
	}

	public DbLanguageId(final String aName, final String aCountry) {
		name = aName;
		country = aCountry;
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

}
