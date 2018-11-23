package ru.dantalian.copvac.persist.orientdb.model;

import javax.persistence.Id;

import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;

import ru.dantalian.copvac.persist.orientdb.api.Index;

@Index(name = "index_name_country", indexType = INDEX_TYPE.NOTUNIQUE, columnList = {"name", "country"})
@Index(name = "index_name_country_variant", indexType = INDEX_TYPE.UNIQUE, columnList = {"name", "country", "variant"})
public class DbLanguage {

	@Id
	private Object id;

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((variant == null) ? 0 : variant.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DbLanguage)) {
			return false;
		}
		final DbLanguage other = (DbLanguage) obj;
		if (country == null) {
			if (other.country != null) {
				return false;
			}
		} else if (!country.equals(other.country)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (variant == null) {
			if (other.variant != null) {
				return false;
			}
		} else if (!variant.equals(other.variant)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "DbLanguage [name=" + name + ", country=" + country + ", text=" + text + ", variant=" + variant + "]";
	}

}
