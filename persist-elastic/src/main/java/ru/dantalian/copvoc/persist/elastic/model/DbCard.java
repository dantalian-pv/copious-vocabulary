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

	@Field
	private String source;

	@Field
	private String target;

	@Field(name = "content", type = "object", subtype = {
			@SubField(path_match="content.*_keyword"),
			@SubField(path_match="content.*_text", type = "text")
	})
	private Map<String, String> fieldsContent;

	public DbCard() {
	}

	public DbCard(final UUID aId, final UUID aVocabularyId, final String aSource, final String aTarget,
			final Map<String, String> aFieldsContent) {
		id = aId;
		vocabularyId = aVocabularyId;
		source = aSource;
		target = aTarget;
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

	public Map<String, String> getFieldsContent() {
		return fieldsContent;
	}

	public void setFieldsContent(final Map<String, String> aFieldsContent) {
		fieldsContent = aFieldsContent;
	}

}
