package ru.dantalian.copvoc.persist.elastic.model;

import java.util.UUID;

import ru.dantalian.copvoc.persist.elastic.model.annotations.Field;
import ru.dantalian.copvoc.persist.elastic.model.annotations.Id;
import ru.dantalian.copvoc.persist.elastic.model.codecs.UUIDCodec;

public class DbVocabulary {

	@Id
	@Field(codec = UUIDCodec.class)
	private UUID id;

	@Field
	private String name;

	@Field
	private String description;

	@Field
	private String user;

	@Field
	private String source;

	@Field
	private String target;

	public DbVocabulary() {
	}

	public DbVocabulary(final UUID aId, final String aName, final String aDescription,
			final String aUser, final String aSource, final String aTarget) {
		id = aId;
		name = aName;
		description = aDescription;
		user = aUser;
		source = aSource;
		target = aTarget;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(final String aDescription) {
		description = aDescription;
	}

	public String getUser() {
		return user;
	}

	public void setUser(final String aUser) {
		user = aUser;
	}

	public String getSource() {
		return source;
	}

	public void setSource(final String aSource) {
		source = aSource;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(final String aTarget) {
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
		if (!(obj instanceof DbVocabulary)) {
			return false;
		}
		final DbVocabulary other = (DbVocabulary) obj;
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
		return "DbVocabulary [id=" + id + ", name=" + name + ", description=" + description
				+ ", user=" + user + ", source=" + source + ", target=" + target + "]";
	}

}
