package ru.dantalian.copvac.persist.sqlite.hibernate.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table( name = "principals",
indexes = {@Index(columnList = "name", name = "name")})
public class HibPrincipal {

	@Id
	@Column(name = "id", length = 36)
	private String id;

	@Column(name = "name", length = 36)
	private String name;

	@Column(name = "description", length = 255)
	private String description;

	public HibPrincipal() {
	}

	public HibPrincipal(final String aId, final String aName, final String aDescription) {
		id = aId;
		name = aName;
		description = aDescription;
	}

	public String getId() {
		return id;
	}

	public void setId(final String aId) {
		id = aId;
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
		if (!(obj instanceof HibPrincipal)) {
			return false;
		}
		final HibPrincipal other = (HibPrincipal) obj;
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
		return "HibPrincipal [id=" + id + ", name=" + name + ", description=" + description + "]";
	}

}
