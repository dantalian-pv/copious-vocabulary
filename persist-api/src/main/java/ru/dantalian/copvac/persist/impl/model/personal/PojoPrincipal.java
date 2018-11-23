package ru.dantalian.copvac.persist.impl.model.personal;

import ru.dantalian.copvac.persist.api.model.personal.Principal;

public class PojoPrincipal implements Principal {

	private String name;

	private String description;

	public PojoPrincipal() {
	}

	public PojoPrincipal(final String aName, final String aDescription) {
		this.name = aName;
		this.description = aDescription;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
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
		if (!(obj instanceof PojoPrincipal)) {
			return false;
		}
		final PojoPrincipal other = (PojoPrincipal) obj;
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
		return "PojoPrincipal [name=" + name + ", description=" + description + "]";
	}

}
