package ru.dantalian.copvoc.persist.impl.model;

import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.CardBatch;
import ru.dantalian.copvoc.persist.api.model.Language;

public class PojoCardBatch implements CardBatch {

	private UUID id;

	private String name;

	private String description;

	private String user;

	private Language source;

	private Language target;

	public PojoCardBatch() {
	}

	public PojoCardBatch(final UUID aId, final String aName, final String aDescription, final String aUser,
			final Language aSource,	final Language aTarget) {
		id = aId;
		name = aName;
		description = aDescription;
		user = aUser;
		source = aSource;
		target = aTarget;
	}

	@Override
	public UUID getId() {
		return id;
	}

	public void setId(final UUID aId) {
		id = aId;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(final String aName) {
		name = aName;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(final String aDescription) {
		description = aDescription;
	}

	@Override
	public String getUser() {
		return user;
	}

	public void setUser(final String aUser) {
		user = aUser;
	}

	@Override
	public Language getSource() {
		return source;
	}

	public void setSource(final Language aSource) {
		source = aSource;
	}

	@Override
	public Language getTarget() {
		return target;
	}

	public void setTarget(final Language aTarget) {
		target = aTarget;
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
		if (!(obj instanceof PojoCardBatch)) {
			return false;
		}
		final PojoCardBatch other = (PojoCardBatch) obj;
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
		return "PojoCardBatch [id=" + id + ", name=" + name + ", description=" + description
				+ ", user=" + user + ", source=" + source + ", target=" + target + "]";
	}

}
