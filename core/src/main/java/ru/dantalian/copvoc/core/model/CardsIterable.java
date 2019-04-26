package ru.dantalian.copvoc.core.model;

import java.beans.Transient;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import ru.dantalian.copvoc.persist.api.PersistCardManager;
import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardFieldContent;
import ru.dantalian.copvoc.persist.api.model.CardStat;
import ru.dantalian.copvoc.persist.api.query.Query;
import ru.dantalian.copvoc.persist.api.query.QueryResult;
import ru.dantalian.copvoc.persist.impl.query.QueryFactory;

public class CardsIterable implements Iterable<Card> {

	private final String user;

	private final UUID vocabularyId;

	private final PersistCardManager cardManager;

	public CardsIterable(final String aUser, final UUID aVocabularyId, final PersistCardManager aCardManager) {
		user = aUser;
		vocabularyId = aVocabularyId;
		cardManager = aCardManager;
	}

	@Override
	public Iterator<Card> iterator() {
		return new CardIterator();
	}

	class CardIterator implements Iterator<Card> {

		private static final int PAGE_SIZE = 30;

		private int from;

		private int current;

		private long total;

		private Iterator<Card> pageIterator;

		@Override
		public boolean hasNext() {
			initPage();
			return current < total;
		}

		@Override
		public Card next() {
			initPage();
			current++;
			final Card card = pageIterator.next();
			return asDtoCard(card);
		}

		private void initPage() {
			try {
				if (pageIterator == null || !pageIterator.hasNext()) {
					if (current == 0) {
						from = 0;
					} else {
						from = from + PAGE_SIZE;
					}
					// Init new page
					final Query query = QueryFactory.newCardsQuery()
						.from(from)
						.limit(PAGE_SIZE)
						.setVocabularyId(vocabularyId)
						.build();
					final QueryResult<Card> result = cardManager.queryCards(user, query);
					total = result.getTotal();
					pageIterator = result.getItems().iterator();
				}
			} catch (final Exception e) {
				throw new NoSuchElementException(e.toString());
			}
		}

		private Card asDtoCard(final Card aCard) {
			return new Card() {

				@Override
				public void setFieldsContent(final Map<String, CardFieldContent> aFields) {
				}

				@Override
				public UUID getVocabularyId() {
					return aCard.getVocabularyId();
				}

				@Override
				public String getTarget() {
					return aCard.getTarget();
				}

				@Override
				@Transient
				public Map<String, CardStat> getStats() {
					return null;
				}

				@Override
				public String getSource() {
					return aCard.getSource();
				}

				@Override
				public UUID getId() {
					return aCard.getId();
				}

				@Override
				public Map<String, CardFieldContent> getFieldsContent() {
					return aCard.getFieldsContent();
				}

				@Override
				public CardFieldContent getContent(final String aFieldName) {
					return null;
				}

				@Override
				public void addFieldsContent(final Map<String, CardFieldContent> aFields) {
				}

				@Override
				public void addFieldContent(final String aFieldName, final CardFieldContent aContent) {
				}
			};
		}

	}

}
