package ru.dantalian.copvoc.core.export.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardFieldContent;

public class CardV1Iterable implements Iterable<CardV1> {

	private final Iterable<Card> cardIterable;

	public CardV1Iterable(final Iterable<Card> aCardIterable) {
		cardIterable = aCardIterable;
	}

	@Override
	public Iterator<CardV1> iterator() {
		final Iterator<Card> iterator = cardIterable.iterator();
		return new Iterator<CardV1>() {

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public CardV1 next() {
				final Card next = iterator.next();
				final Map<String, CardFieldContent> fieldsContent = next.getFieldsContent();
				final Map<String, String> content = new HashMap<>();
				for (final Entry<String, CardFieldContent> entry: fieldsContent.entrySet()) {
					content.put(entry.getKey(), entry.getValue().getContent());
				}
				return new CardV1(content);
			}
		};
	}

}
