package ru.dantalian.copvac.persist.orientdb.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Embeddable;

@Embeddable
public class DpPrincipalId implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID id;

	private String name;

	public DpPrincipalId() {
	}

	public DpPrincipalId(final UUID aId, final String aName) {
		id = aId;
		name = aName;
	}

	public UUID getId() {
		return id;
	}

	public void setId(final UUID aId) {
		id = aId;
	}

	public String getName() {
		return name;
	}

	public void setName(final String aName) {
		name = aName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (!(obj instanceof DpPrincipalId)) {
			return false;
		}
		final DpPrincipalId other = (DpPrincipalId) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "DpPrincipalId [id=" + id + ", name=" + name + "]";
	}

}
