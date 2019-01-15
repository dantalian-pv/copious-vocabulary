package ru.dantalian.copvoc.persist.elastic.model;

import java.util.Map;
import java.util.UUID;

import ru.dantalian.copvoc.persist.elastic.model.annotations.Field;
import ru.dantalian.copvoc.persist.elastic.model.annotations.Id;
import ru.dantalian.copvoc.persist.elastic.model.annotations.SubField;
import ru.dantalian.copvoc.persist.elastic.model.codecs.UUIDCodec;

public class DbCard {

	@Id
	@Field(codec = UUIDCodec.class)
	private UUID id;

	@Field(name = "vocabulary_id", codec = UUIDCodec.class)
	private UUID vocabularyId;

	@Field(name = "content", type = "object", subtype = {
			@SubField(path_match="content.*_keyword"),
			@SubField(path_match="content.*_text", type = "text")
	})
	private Map<String, String> fieldsContent;

	public DbCard() {
	}

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
