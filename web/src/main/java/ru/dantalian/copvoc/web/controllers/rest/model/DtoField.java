package ru.dantalian.copvoc.web.controllers.rest.model;

public class DtoField {

	private String vocabularyId;

	private String name;

	private String	type;

	private Integer order;

	private boolean system;

	public DtoField() {
	}

	public DtoField(final String aVocabularyId, final String aName,
			final String aType, final Integer aOrder, final boolean aSystem) {
		vocabularyId = aVocabularyId;
		name = aName;
		type = aType;
		order = aOrder;
		system = aSystem;
	}

	public String getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(final String aVocabularyId) {
		vocabularyId = aVocabularyId;
	}

	public String getName() {
		return name;
	}

	public void setName(final String aName) {
		name = aName;
	}

	public String getType() {
		return type;
	}

	public void setType(final String aType) {
		type = aType;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(final Integer aOrder) {
		order = aOrder;
	}

	public boolean isSystem() {
		return system;
	}

	public void setSystem(final boolean aSystem) {
		system = aSystem;
	}

	@Override
	public String toString() {
		return "DtoField [vocabularyId=" + vocabularyId + ", name=" + name + ", type=" + type +
				", order=" + order + ", system=" + system + "]";
	}

}
