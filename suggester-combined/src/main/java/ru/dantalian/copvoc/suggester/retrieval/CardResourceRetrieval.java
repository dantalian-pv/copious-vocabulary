package ru.dantalian.copvoc.suggester.retrieval;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import ru.dantalian.copvoc.persist.api.PersistCardManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardFieldContent;
import ru.dantalian.copvoc.suggester.api.SuggestException;
import ru.dantalian.copvoc.suggester.api.UniversalRetrieval;

@Component("card")
@Order(10)
public class CardResourceRetrieval implements UniversalRetrieval {

	@Autowired
	private PersistCardManager cardManager;

	@Override
	public boolean accept(final URI aSource) {
		final String scheme = aSource.getScheme();
		return "card".equals(scheme);
	}

	@Override
	public Map<String, Object> retrieve(final String aUser, final URI aSource) throws SuggestException {
		try {
			final UUID vocId = UUID.fromString(aSource.getHost());
			final String[] split = aSource.getPath().split("/");
			final UUID cardId = UUID.fromString(split[1]);
			final Card card = cardManager.getCard(aUser, vocId, cardId);
			if (card == null) {
				return Collections.emptyMap();
			}
			final Map<String, Object> map = new HashMap<>();
			for (final Entry<String, CardFieldContent> entry: card.getFieldsContent().entrySet()) {
				map.put(entry.getKey(), entry.getValue().getContent());
			}
			return map;
		} catch (final PersistException e) {
			throw new SuggestException("Failed to retrieve " + aSource, e);
		}
	}

}
