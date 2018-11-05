package ru.dantalian.copvac.persist.nitrite.hibernate.model;

import java.io.Serializable;

import org.dizitart.no2.IndexType;
import org.dizitart.no2.objects.Index;
import org.dizitart.no2.objects.Indices;

@Indices({
  @Index(value = "name", type = IndexType.NonUnique),
  @Index(value = "country", type = IndexType.NonUnique),
  @Index(value = "variant", type = IndexType.NonUnique)
})
public class DbLanguage implements Serializable {

	private static final long serialVersionUID = -6393462192785396179L;

	private String name;

	private String country;

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
