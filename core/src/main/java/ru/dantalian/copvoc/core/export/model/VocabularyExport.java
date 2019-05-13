package ru.dantalian.copvoc.core.export.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VocabularyExport {

	private int version;

	private VocabularyV1 vocabulary;

	private VocabularyViewV1 view;

	private List<CardFieldV1> fields;

	private Iterable<CardV1> cards;

	public VocabularyExport() {
	}

	public VocabularyExport(final int aVersion, final VocabularyV1 aVocabulary,
			final VocabularyViewV1 aView, final List<CardFieldV1> aFields,
			final Iterable<CardV1> aCards) {
		version = aVersion;
		vocabulary = aVocabulary;
		view = aView;
		fields = aFields;
		cards = aCards;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(final int aVersion) {
		version = aVersion;
	}

	public VocabularyV1 getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(final VocabularyV1 aVocabulary) {
		vocabulary = aVocabulary;
	}

	public VocabularyViewV1 getView() {
		return view;
	}

	public void setView(final VocabularyViewV1 aView) {
		view = aView;
	}

	public List<CardFieldV1> getFields() {
		return fields;
	}

	public void setFields(final List<CardFieldV1> aFields) {
		fields = aFields;
	}

	public Iterable<CardV1> getCards() {
		return cards;
	}

	public void setCards(final Iterable<CardV1> aCards) {
		cards = aCards;
	}

}
