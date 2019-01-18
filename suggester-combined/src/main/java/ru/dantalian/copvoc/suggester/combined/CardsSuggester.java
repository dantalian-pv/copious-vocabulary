package ru.dantalian.copvoc.suggester.combined;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import ru.dantalian.copvoc.persist.api.PersistCardFieldManager;
import ru.dantalian.copvoc.persist.api.PersistCardManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFieldContent;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;
import ru.dantalian.copvoc.persist.api.query.BoolExpressionBuilder;
import ru.dantalian.copvoc.persist.api.query.CardsQueryBuilder;
import ru.dantalian.copvoc.persist.impl.query.QueryFactory;
import ru.dantalian.copvoc.suggester.api.SuggestException;
import ru.dantalian.copvoc.suggester.api.SuggestQuery;
import ru.dantalian.copvoc.suggester.api.SuggestQueryType;
import ru.dantalian.copvoc.suggester.api.Suggester;
import ru.dantalian.copvoc.suggester.api.model.Suggest;
import ru.dantalian.copvoc.suggester.combined.model.PojoSuggest;

@Component("cards")
@Order(10)
public class CardsSuggester implements Suggester {

	@Autowired
	private PersistCardManager cardManager;

	@Autowired
	private PersistCardFieldManager fieldManager;

	@Override
	public boolean accept(final SuggestQueryType aType) {
		return aType == SuggestQueryType.STRING || aType == SuggestQueryType.TEXT;
	}

	@Override
	public List<Suggest> suggest(final String aUser, final SuggestQuery aQuery) throws SuggestException {
		try {
			final List<Suggest> suggests = new LinkedList<>();

			final List<CardField> fields = fieldManager.listFields(aUser, null);
			final String key = aQuery.getWhere().getKey() == null || aQuery.getWhere().getKey().isEmpty()
					? null : aQuery.getWhere().getKey().toLowerCase();
			final CardsQueryBuilder cardsQuery = QueryFactory.newCardsQuery();
			final BoolExpressionBuilder bool = QueryFactory.bool();
			for (final CardField field: fields) {
				if (key == null || field.getName().toLowerCase().contains(key)) {
					addQueryByType(aQuery, bool, field);
				}
			}
			if (aQuery.getNot() != null) {
				bool.not(QueryFactory.term(aQuery.getNot().getKey(), aQuery.getNot().getValue(), false));
			}
			cardsQuery.where(bool.build());
			final List<Card> queryCards = cardManager.queryCards(aUser, cardsQuery.build());
			for (final Card card: queryCards) {
				suggests.addAll(asSuggest(aUser, card, aQuery.getWhere().getKey(), aQuery.getType()));
			}
			return suggests;
		} catch (final PersistException e) {
			throw new SuggestException("Failed to query cards", e);
		}
	}

	private void addQueryByType(final SuggestQuery aQuery, final BoolExpressionBuilder bool, final CardField field) {
		switch (aQuery.getType()) {
			case FIELD:
				// Just ignore
				break;
			case STRING:
				bool.should(QueryFactory.term("content." + field.getName() + "_keyword", aQuery.getWhere().getValue() + "*", true));
				break;
			case TEXT:
				bool.should(QueryFactory.term("content." + field.getName() + "_text", aQuery.getWhere().getValue() + "*", true));
				break;
			default:
				throw new IllegalArgumentException("Unknown query type: " + aQuery.getType());
		}
	}

	private List<Suggest> asSuggest(final String aUser, final Card aCard, final String aKey, final SuggestQueryType aType)
			throws PersistException {
		final List<Suggest> suggests = new LinkedList<>();
		final Map<String, CardFieldContent> fieldsContent = aCard.getFieldsContent();
		for (final Entry<String, CardFieldContent> entry: fieldsContent.entrySet()) {
			final String name = entry.getKey().replaceAll("_\\w+$", "");
			if (!name.toLowerCase().contains(aKey.toLowerCase())) {
				continue;
			}
			final CardField field = fieldManager.getField(aUser, aCard.getVocabularyId(), name);
			final CardFieldContent content = entry.getValue();
			if (isFits(aType, field)) {
				suggests.add(new PojoSuggest(URI.create("card://" + aCard.getVocabularyId() + "/" + aCard.getId() + "/" + name), name, content.getContent(), 1.0d));
			}
		}
		return suggests;
	}

	private boolean isFits(final SuggestQueryType aType, final CardField field) {
		switch (aType) {
			case STRING:
				return field.getType() == CardFiledType.ANSWER || field.getType() == CardFiledType.STRING;
			case TEXT:
				return field.getType() == CardFiledType.TEXT || field.getType() == CardFiledType.MARKUP;
			default:
				throw new IllegalArgumentException("Unknown query type " + aType);
		}
	}

}
