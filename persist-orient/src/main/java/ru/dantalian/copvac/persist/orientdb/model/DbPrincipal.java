package ru.dantalian.copvac.persist.orientdb.model;

import javax.persistence.Id;

import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;

import ru.dantalian.copvac.persist.orientdb.api.Index;

@Index(name = "name_index", columnList="name", indexType = INDEX_TYPE.UNIQUE)
public class DbPrincipal {

	@Id
	private Object id;

	private String name;

	private String description;

	public DbPrincipal() {
	}

	public DbPrincipal(final String aName, final String aDescription) {
		name = aName;
		description = aDescription;
	}

	public String getName() {
		return name;
	}

	public void setName(final String aName) {
		name = aName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String aDescription) {
		description = aDescription;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (!(obj instanceof DbPrincipal)) {
			return false;
		}
		final DbPrincipal other = (DbPrincipal) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "DbPrincipal [id=" + id + ", name=" + name + ", description=" + description + "]";
	}

}
