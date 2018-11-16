package ru.dantalian.copvac.persist.orientdb.model;

import java.util.UUID;

import javax.persistence.EmbeddedId;

public class DbPrincipal {

	@EmbeddedId
	private DpPrincipalId id;

	private String description;

	public DbPrincipal() {
	}

	public DbPrincipal(final UUID aId, final String aName, final String aDescription) {
		id = new DpPrincipalId(aId, aName);
		description = aDescription;
	}

	public DpPrincipalId getId() {
		return id;
	}

	public void setId(final DpPrincipalId aId) {
		id = aId;
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
		return "DbPrincipal [id=" + id + ", description=" + description + "]";
	}

}
