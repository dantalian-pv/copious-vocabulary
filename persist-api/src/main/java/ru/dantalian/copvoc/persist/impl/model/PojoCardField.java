package ru.dantalian.copvoc.persist.impl.model;

import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;

public class PojoCardField implements CardField {

	private UUID vocabularyId;

	private String name;

	private CardFiledType	type;

	private Integer order;

	private boolean system;

	public PojoCardField() {
	}

	public PojoCardField(final UUID aVocabularyId, final String aName,
			final CardFiledType aType, final Integer aOrder, final boolean aSystem) {
		vocabularyId = aVocabularyId;
		name = aName;
		type = aType;
		order = aOrder;
		system = aSystem;
	}

	@Override
	public UUID getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(final UUID aVocabularyId) {
		vocabularyId = aVocabularyId;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(final String aName) {
		name = aName;
	}

	@Override
	public CardFiledType getType() {
		return type;
	}

	public void setType(final CardFiledType aType) {
		type = aType;
	}

	@Override
	public Integer getOrder() {
		return order;
	}

	public void setOrder(final Integer aOrder) {
		order = aOrder;
	}

	@Override
	public boolean isSystem() {
		return system;
	}

	public void setSystem(final boolean aSystem) {
		system = aSystem;
	}

	@Override
	public String toString() {
		return "PojoCardField [vocabularyId=" + vocabularyId + ", name=" + name + ", type=" + type
				+ ", order=" + order + ", system=" + system + "]";
	}

}
