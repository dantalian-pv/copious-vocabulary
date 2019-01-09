package ru.dantalian.copvoc.web.controllers.rest.model;

public class DtoField {

	private String vocabularyId;

	private String name;

	private String	type;

	public DtoField() {
	}

	public DtoField(final String aVocabularyId, final String aName,
			final String aType) {
		vocabularyId = aVocabularyId;
		name = aName;
		type = aType;
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

	@Override
	public String toString() {
		return "DtoField [vocabularyId=" + vocabularyId + ", name=" + name + ", type=" + type + "]";
	}

}
