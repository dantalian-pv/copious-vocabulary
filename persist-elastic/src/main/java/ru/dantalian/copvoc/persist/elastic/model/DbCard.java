package ru.dantalian.copvoc.persist.elastic.model;

import java.util.Map;
import java.util.UUID;

public class DbCard {

	private UUID id;

	private UUID vocabularyId;

	private Map<String, String> fieldsContent;

	public DbCard(final UUID aId, final UUID aVocabularyId, final Map<String, String> aFieldsContent) {
		id = aId;
		vocabularyId = aVocabularyId;
		fieldsContent = aFieldsContent;
	}

	public UUID getId() {
		return id;
	}

	public void setId(final UUID aId) {
		id = aId;
	}

	public UUID getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(final UUID aVocabularyId) {
		vocabularyId = aVocabularyId;
	}

	public Map<String, String> getFieldsContent() {
		return fieldsContent;
	}

	public void setFieldsContent(final Map<String, String> aFieldsContent) {
		fieldsContent = aFieldsContent;
	}

}
