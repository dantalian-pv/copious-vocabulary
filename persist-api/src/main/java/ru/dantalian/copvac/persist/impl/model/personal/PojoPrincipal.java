package ru.dantalian.copvac.persist.impl.model.personal;

import java.util.UUID;

import ru.dantalian.copvac.persist.api.model.personal.Principal;

public class PojoPrincipal implements Principal {

	private UUID id;

	private String name;

	private String description;

	public PojoPrincipal() {
	}

	public PojoPrincipal(final UUID aId, final String aName, final String aDescription) {
		this.id = aId;
		this.name = aName;
		this.description = aDescription;
	}

	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setId(final UUID aId) {
		id = aId;
	}

	public void setName(final String aName) {
		name = aName;
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
	public boolean equals(final Object aObj) {
		if (this == aObj) {
			return true;
		}
		if (aObj == null) {
			return false;
		}
		if (!(aObj instanceof PojoPrincipal)) {
			return false;
		}
		final PojoPrincipal other = (PojoPrincipal) aObj;
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
		return "PojoPrincipal [id=" + id + ", name=" + name + ", description=" + description + "]";
	}

}
