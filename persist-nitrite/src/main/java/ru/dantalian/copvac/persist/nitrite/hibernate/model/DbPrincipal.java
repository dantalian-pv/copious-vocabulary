package ru.dantalian.copvac.persist.nitrite.hibernate.model;

import java.io.Serializable;

import org.dizitart.no2.IndexType;
import org.dizitart.no2.objects.Index;
import org.dizitart.no2.objects.Indices;

@Indices({
  @Index(value = "name", type = IndexType.Unique),
  @Index(value = "id", type = IndexType.Unique)
})
public class DbPrincipal implements Serializable {

	private static final long serialVersionUID = -6384525174653930316L;

	private String id;

	private String name;

	private String description;

	public DbPrincipal() {
	}

	public DbPrincipal(final String aId, final String aName, final String aDescription) {
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
