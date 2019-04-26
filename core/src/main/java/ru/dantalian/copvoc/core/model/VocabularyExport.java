package ru.dantalian.copvoc.core.model;

import java.util.List;

import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.Vocabulary;
import ru.dantalian.copvoc.persist.api.model.VocabularyView;

public class VocabularyExport {

	private final int version;

	private final Vocabulary vocabulary;

	private final VocabularyView view;

	private final List<CardField> fields;

	private final Iterable<Card> cards;

	public VocabularyExport(final int aVersion, final Vocabulary aVocabulary, final VocabularyView aView, final List<CardField> aFields,
			final Iterable<Card> aCards) {
		version = aVersion;
		vocabulary = aVocabulary;
		view = aView;
		fields = aFields;
		cards = aCards;
	}

	public int getVersion() {
		return version;
	}

	public Vocabulary getVocabulary() {
		return vocabulary;
	}

	public VocabularyView getView() {
		return view;
	}

	public List<CardField> getFields() {
		return fields;
	}

	public Iterable<Card> getCards() {
		return cards;
	}

}
