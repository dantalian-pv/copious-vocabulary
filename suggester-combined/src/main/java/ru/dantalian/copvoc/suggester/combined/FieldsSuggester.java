package ru.dantalian.copvoc.suggester.combined;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import ru.dantalian.copvoc.persist.api.PersistCardFieldManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistVocabularyManager;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.Vocabulary;
import ru.dantalian.copvoc.suggester.api.SuggestException;
import ru.dantalian.copvoc.suggester.api.SuggestQuery;
import ru.dantalian.copvoc.suggester.api.SuggestQueryType;
import ru.dantalian.copvoc.suggester.api.Suggester;
import ru.dantalian.copvoc.suggester.api.model.Pair;
import ru.dantalian.copvoc.suggester.api.model.Suggest;
import ru.dantalian.copvoc.suggester.combined.model.PojoSuggest;

@Component("fields")
@Order(10)
public class FieldsSuggester implements Suggester {

	@Autowired
	private PersistCardFieldManager fieldManager;

	@Autowired
	private PersistVocabularyManager vocManager;

	@Override
	public boolean accept(final Pair<String, String> aSourceTarget, final SuggestQueryType aType) {
		return aType == SuggestQueryType.FIELD;
	}

	@Override
	public List<Suggest> suggest(final String aUser, final SuggestQuery aQuery) throws SuggestException {
		try {
			final List<Suggest> suggests = new LinkedList<>();
			final List<CardField> fields = fieldManager.listFields(aUser, null);
			final String value = aQuery.getWhere().getValue().toLowerCase();
			UUID vocId = null;
			if (aQuery.getNot() != null && "vocabulary_id".equals(aQuery.getNot().getKey())) {
				vocId = UUID.fromString(aQuery.getNot().getValue());
			}
			for (final CardField field: fields) {
				if (field.getName().toLowerCase().contains(value)
						&& (vocId == null || !vocId.equals(field.getVocabularyId()))) {
					final Vocabulary voc = vocManager.getVocabulary(aUser, field.getVocabularyId());
					suggests.add(asSuggest(voc, field));
				}
			}
			return suggests;
		} catch (final PersistException e) {
			throw new SuggestException("Failed to query cards", e);
		}
	}

	private Suggest asSuggest(final Vocabulary aVoc, final CardField aField) {

		return new PojoSuggest(URI.create("field://" + aField.getVocabularyId() + "/" + aField.getName()),
				aVoc.getName(),
				aField.getType().name(), aField.getName(), "", 1.0d);
	}

}
