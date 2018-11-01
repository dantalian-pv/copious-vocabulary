package ru.dantalian.copvac.persist.impl.model.personal;

import ru.dantalian.copvac.persist.api.model.personal.Principal;

public class PojoPrincipal implements Principal {

	private String id;

	private String name;

	private String description;

	public PojoPrincipal() {
	}

	public PojoPrincipal(final String id, final String name, final String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	@Override
	public String getId() {
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
		if (!(obj instanceof PojoPrincipal)) {
			return false;
		}
		final PojoPrincipal other = (PojoPrincipal) obj;
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
