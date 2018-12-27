package ru.dantalian.copvoc.web.controllers.rest.model;

import java.util.List;

public class DtoCard {

	private String id;

	private String vocabularyId;

	private List<DtoCardContent> content;

	public DtoCard() {
	}

	public DtoCard(final String aId, final String aVocabularyId, final List<DtoCardContent> aContent) {
		id = aId;
		vocabularyId = aVocabularyId;
		content = aContent;
	}

	public String getId() {
		return id;
	}

	public void setId(final String aId) {
		id = aId;
	}

	public String getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(final String aVocabularyId) {
		vocabularyId = aVocabularyId;
	}

	public List<DtoCardContent> getContent() {
		return content;
	}

	public void setContent(final List<DtoCardContent> aContent) {
		content = aContent;
	}

}
