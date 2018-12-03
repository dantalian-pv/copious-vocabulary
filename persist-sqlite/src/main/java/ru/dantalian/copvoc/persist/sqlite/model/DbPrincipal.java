package ru.dantalian.copvoc.persist.sqlite.model;

public class DbPrincipal {

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
		if (!(obj instanceof DbPrincipal)) {
			return false;
		}
		final DbPrincipal other = (DbPrincipal) obj;
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
		return "DbPrincipal [name=" + name + ", description=" + description + "]";
	}

}
