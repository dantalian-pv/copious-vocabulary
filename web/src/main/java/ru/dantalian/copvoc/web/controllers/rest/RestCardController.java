package ru.dantalian.copvoc.web.controllers.rest;

import java.security.Principal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ru.dantalian.copvoc.persist.api.PersistCardManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardFieldContent;
import ru.dantalian.copvoc.persist.impl.query.QueryFactory;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoCard;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoCardContent;

@RestController
@RequestMapping(value = "/v1/api/cards", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestCardController {

	@Autowired
	private PersistCardManager mCardManager;

	@RequestMapping(value = "/{voc_id}", method = RequestMethod.GET)
	public List<DtoCard> listCards(@PathVariable(value = "voc_id") final String aVocabularyId,
			final Principal aPrincipal) throws RestException {
		try {
			final String user = aPrincipal.getName();
			return mCardManager.queryCards(user, QueryFactory.newCardsQuery()
					.setVocabularyId(UUID.fromString(aVocabularyId)).build())
					.stream()
					.map(this::asDtoCard)
					.collect(Collectors.toList());
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/{voc_id}/{id}", method = RequestMethod.GET)
	public DtoCard getCard(@PathVariable(value = "voc_id") final String aVocId,
			@PathVariable(value = "id") final String aId, final Principal aPrincipal)
					throws RestException {
		try {
			final String user = aPrincipal.getName();
			final Card card = mCardManager.getCard(user, UUID.fromString(aVocId), UUID.fromString(aId));
			if (card == null) {
				throw new PersistException("Card with id: " + aId + " not found");
			}
			return asDtoCard(card);
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public DtoCard createCard(final Principal aPrincipal, @RequestBody final DtoCard aCard)
			throws RestException {
		try {
			final String user = aPrincipal.getName();
			final Map<String, String> map = asMap(aCard.getContent());
			final Card card = mCardManager.createCard(user, UUID.fromString(aCard.getVocabularyId()), map);
			return asDtoCard(card);
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateCard(final Principal aPrincipal, @RequestBody final DtoCard aCard)
			throws RestException {
		try {
			final String user = aPrincipal.getName();
			final Map<String, String> map = asMap(aCard.getContent());
			mCardManager.updateCard(user, UUID.fromString(aCard.getVocabularyId()),
					UUID.fromString(aCard.getId()), map);
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

	private Map<String, String> asMap(final List<DtoCardContent> aContent) {
		final Map<String, String> map = new HashMap<>();
		for (final DtoCardContent item: aContent) {
			map.put(item.getName(), item.getText());
		}
		return map;
	}

	private DtoCard asDtoCard(final Card aCard) {
		if (aCard == null) {
			return null;
		}
		final List<DtoCardContent> list = new LinkedList<>();
		final Map<String, CardFieldContent> content = aCard.getFieldsContent();
		for(final Entry<String, CardFieldContent> entry: content.entrySet()) {
			list.add(new DtoCardContent(entry.getKey(), entry.getValue().getContent()));
		}
		return new DtoCard(aCard.getId().toString(),
				aCard.getVocabularyId().toString(), list);
	}

}
