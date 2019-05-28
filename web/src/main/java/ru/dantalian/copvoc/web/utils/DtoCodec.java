package ru.dantalian.copvoc.web.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFieldContent;
import ru.dantalian.copvoc.persist.api.model.CardStat;
import ru.dantalian.copvoc.persist.api.model.Language;
import ru.dantalian.copvoc.persist.api.model.Training;
import ru.dantalian.copvoc.persist.api.model.Vocabulary;
import ru.dantalian.copvoc.persist.api.model.VocabularyView;
import ru.dantalian.copvoc.persist.api.utils.LanguageUtils;
import ru.dantalian.copvoc.persist.impl.model.PojoVocabulary;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoCard;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoCardContent;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoField;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoLanguage;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoTrainingStats;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoView;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoVocabulary;

public final class DtoCodec {

	private DtoCodec() {
	}

	public static Language asLanguage(final String aLanguage) {
		return LanguageUtils.asLanguage(aLanguage);
	}

	public static DtoLanguage asDtoLanguage(final Language aLanguage) {
		if (aLanguage == null) {
			return null;
		}
		return new DtoLanguage(LanguageUtils.asString(aLanguage), aLanguage.getName(), aLanguage.getCountry(),
				aLanguage.getVariant(), aLanguage.getText());
	}

	public static DtoVocabulary asDtoVocabulary(final Vocabulary aVocabulary) {
		if (aVocabulary == null) {
			return null;
		}
		return new DtoVocabulary(aVocabulary.getId().toString(), aVocabulary.getName(), aVocabulary.getDescription(),
				LanguageUtils.asString(aVocabulary.getSource()), aVocabulary.getSource().getText(),
				LanguageUtils.asString(aVocabulary.getTarget()), aVocabulary.getTarget().getText(),
				aVocabulary.isShared());
	}

	public static Vocabulary asVocabulary(final String aUser, final DtoVocabulary aDtoVocabulary) {
		return new PojoVocabulary(UUID.fromString(aDtoVocabulary.getId()), aDtoVocabulary.getName(),
				aDtoVocabulary.getDescription(), aUser,
				asLanguage(aDtoVocabulary.getSourceId()), asLanguage(aDtoVocabulary.getTargetId()),
				aDtoVocabulary.isShared());
	}

	public static DtoView asDtoView(final VocabularyView aView) {
		if (aView == null) {
			return null;
		}
		return new DtoView(aView.getVocabularyId().toString(), aView.getCss(), aView.getFront(), aView.getBack());
	}

	public static DtoField asDtoField(final CardField aField) {
		if (aField == null) {
			return null;
		}
		return new DtoField(aField.getVocabularyId().toString(), aField.getName(), aField.getType().name(),
				aField.getOrder(), aField.isSystem());
	}

	public static DtoCard asDtoCard(final Card aCard) {
		if (aCard == null) {
			return null;
		}
		final List<DtoCardContent> list = new LinkedList<>();
		final Map<String, CardFieldContent> content = aCard.getFieldsContent();
		for(final Entry<String, CardFieldContent> entry: content.entrySet()) {
			list.add(new DtoCardContent(entry.getKey(), entry.getValue().getContent()));
		}
		return new DtoCard(aCard.getId().toString(),
				aCard.getVocabularyId().toString(),
				aCard.getSource(),
				list);
	}

	public static DtoTrainingStats asDtoTrainingStats(final Training aTraining) {
		if (aTraining == null) {
			return null;
		}
		final Map<String, Object> stats = new HashMap<>();
		for (final Entry<String, CardStat> entry: aTraining.getStats().entrySet()) {
			stats.put(entry.getKey(), entry.getValue());
		}
		return new DtoTrainingStats(aTraining.getId().toString(), aTraining.getVocabularyId().toString(),
				stats, aTraining.getSize(), aTraining.isFinished());
	}

}
