package ru.dantalian.copvoc.persist.impl.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardFieldContent;
import ru.dantalian.copvoc.persist.api.model.CardStat;

public class PojoCard implements Card {

	private UUID id;

	private UUID vocabularyId;

	private String source;

	private String target;

	private Map<String, CardFieldContent> fieldsContent;

	private Map<String, CardStat> stats;

	public PojoCard() {
	}

	public PojoCard(final UUID aId, final UUID aVocabularyId,
			final String aSource, final String aTarget,
			final Map<String, CardFieldContent> aFieldsContent,
			final Map<String, CardStat> aStats) {
		id = aId;
		vocabularyId = aVocabularyId;
		source = aSource;
		target = aTarget;
		fieldsContent = aFieldsContent;
		stats = aStats;
	}

	@Override
	public UUID getId() {
		return id;
	}

	public void setId(final UUID aId) {
		id = aId;
	}

	@Override
	public UUID getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(final UUID aVocabularyId) {
		vocabularyId = aVocabularyId;
	}

	@Override
	public String getSource() {
		return source;
	}

	public void setSource(final String aSource) {
		source = aSource;
	}

	@Override
	public String getTarget() {
		return target;
	}

	public void setTarget(final String aTarget) {
		target = aTarget;
	}

	@Override
	public Map<String, CardFieldContent> getFieldsContent() {
		return fieldsContent == null ? Collections.emptyMap() : fieldsContent;
	}

	@Override
	public void setFieldsContent(final Map<String, CardFieldContent> aFieldsContent) {
		fieldsContent = aFieldsContent;
	}

	@Override
	public CardFieldContent getContent(final String aFieldName) {
		return fieldsContent.get(aFieldName);
	}

	@Override
	public void addFieldsContent(final Map<String, CardFieldContent> aFields) {
		if (fieldsContent == null) {
			fieldsContent = new HashMap<>();
		}
		fieldsContent.putAll(aFields);
	}

	@Override
	public void addFieldContent(final String aField, final CardFieldContent aContent) {
		if (fieldsContent == null) {
			fieldsContent = new HashMap<>();
		}
		fieldsContent.put(aField, aContent);
	}

	@Override
	public Map<String, CardStat> getStats() {
		return stats == null ? Collections.emptyMap() : stats;
	}

	public void setStats(final Map<String, CardStat> aStats) {
		stats = aStats;
	}

	@Override
	public String toString() {
		return "PojoCard [id=" + id + ", vocabularyId=" + vocabularyId + ", fieldsContent=" + fieldsContent + "]";
	}

}
