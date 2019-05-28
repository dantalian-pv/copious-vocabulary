package ru.dantalian.copvoc.web.controllers.rest;

import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ru.dantalian.copvoc.core.stats.DefaultCardStats;
import ru.dantalian.copvoc.core.utils.CardStatFactory;
import ru.dantalian.copvoc.core.utils.StatsUtils;
import ru.dantalian.copvoc.persist.api.PersistCardFieldManager;
import ru.dantalian.copvoc.persist.api.PersistCardManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistVocabularyManager;
import ru.dantalian.copvoc.persist.api.analyse.WordHighlighter;
import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFieldContent;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;
import ru.dantalian.copvoc.persist.api.model.CardStat;
import ru.dantalian.copvoc.persist.api.model.Vocabulary;
import ru.dantalian.copvoc.persist.api.query.QueryFactory;
import ru.dantalian.copvoc.persist.api.query.QueryResult;
import ru.dantalian.copvoc.persist.api.stats.StatAction;
import ru.dantalian.copvoc.persist.impl.model.PojoCardFieldContent;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoCard;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoCardContent;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoQueryResult;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoVoid;
import ru.dantalian.copvoc.web.utils.DtoCodec;

@RestController
@RequestMapping(value = "/v1/api/cards", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestCardController {

	@Autowired
	private PersistCardManager cardManager;

	@Autowired
	private PersistVocabularyManager vocManager;

	@Autowired
	private PersistCardFieldManager fieldManager;

	@Autowired
	private WordHighlighter highlighter;

	@RequestMapping(value = "/{voc_id}", method = RequestMethod.GET)
	public DtoQueryResult<DtoCard> listCards(@PathVariable(value = "voc_id") final String aVocabularyId,
			@RequestParam(value = "from", defaultValue = "0") final int aFrom,
			@RequestParam(value = "limit", defaultValue = "30") final int aLimit,
			@RequestParam(value = "highlight", defaultValue = "false") final boolean aHighlight,
			final Principal aPrincipal) throws RestException {
		try {
			final String user = aPrincipal.getName();
			final UUID vocId = UUID.fromString(aVocabularyId);
			final Vocabulary vocabulary = vocManager.getVocabulary(user, vocId);
			if (vocabulary == null) {
				throw new RestException("No vocabulart found with id: " + aVocabularyId);
			}
			final List<CardField> fields = fieldManager.listFields(user, vocId);
			final Map<String, CardField> fieldMap = new HashMap<>();
			for (final CardField field: fields) {
				fieldMap.put(field.getName(), field);
			}

			final QueryResult<Card> queryResult = cardManager.queryCards(user, QueryFactory.newCardsQuery()
					.setVocabularyId(vocId).build());
			final List<DtoCard> list = queryResult.getItems().stream()
					.peek(aCard -> highlightCard(aCard, vocabulary, fieldMap, aHighlight))
				.map(DtoCodec::asDtoCard)
				.collect(Collectors.toList());
			return new DtoQueryResult<>(list, queryResult.getTotal(), aFrom, aLimit);
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

	private void highlightCard(final Card aCard, final Vocabulary aVoc, final Map<String, CardField> aFieldsMap, final boolean aHighlight) {
		if (!aHighlight) {
			return;
		}
		final CardFieldContent word = aCard.getContent("word");
		final CardFieldContent answer = aCard.getContent("translation");

		final Map<String, CardFieldContent> map = new HashMap<>();

		for (final Entry<String, CardFieldContent> entry: aCard.getFieldsContent().entrySet()) {
			final String name = entry.getKey();
			final CardField field = aFieldsMap.get(name);
			final CardFieldContent content = entry.getValue();

			String text = content.getContent();
			if (CardFiledType.isText(field.getType())) {
				try {
					text = highlighter.replace(text, answer.getContent(), word.getContent(), aVoc.getTarget());
				} catch (final PersistException e) {
					throw new RuntimeException("Failed to highlight for " + content, e);
				}
			}

			final PojoCardFieldContent newContent = new PojoCardFieldContent(
					content.getCardId(), aVoc.getId(), name, text);
			map.put(name, newContent);
		}
		aCard.setFieldsContent(map);
	}

	@RequestMapping(value = "/{voc_id}/{id}", method = RequestMethod.GET)
	public DtoCard getCard(@PathVariable(value = "voc_id") final String aVocId,
			@PathVariable(value = "id") final String aId,
			@RequestParam(value = "highlight", defaultValue = "false") final boolean aHighlight,
			final Principal aPrincipal)
					throws RestException {
		try {
			final String user = aPrincipal.getName();
			final UUID vocId = UUID.fromString(aVocId);
			final Vocabulary vocabulary = vocManager.getVocabulary(user, vocId);
			if (vocabulary == null) {
				throw new RestException("No vocabulart found with id: " + aVocId);
			}
			final List<CardField> fields = fieldManager.listFields(user, vocId);
			final Map<String, CardField> fieldMap = new HashMap<>();
			for (final CardField field: fields) {
				fieldMap.put(field.getName(), field);
			}

			final Card card = cardManager.getCard(user, vocId, UUID.fromString(aId));
			if (card == null) {
				throw new PersistException("Card with id: " + aId + " not found");
			}
			highlightCard(card, vocabulary, fieldMap, aHighlight);

			return DtoCodec.asDtoCard(card);
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
			final Map<String, CardStat> stats = StatsUtils.defaultStats();
			final Card card = cardManager.createCard(user,
					UUID.fromString(aCard.getVocabularyId()),
					aCard.getSource(),
					map, stats);
			updateSourceStats(user, aCard.getSource(), StatAction.ICREMENT);
			return DtoCodec.asDtoCard(card);
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

	private void updateSourceStats(final String aUser, final String aSource, final StatAction aAction)
			throws PersistException {
		if (aSource == null || aSource.isEmpty() || !aSource.startsWith("card://")) {
			return;
		}
		CompletableFuture.runAsync(() -> {
			final URI sourceUri = URI.create(aSource);
			final UUID vocId = UUID.fromString(sourceUri.getHost());
			final String[] split = sourceUri.getPath().split("/");
			final UUID cardId = UUID.fromString(split[1]);
			try {
				cardManager.updateStatForCard(aUser, vocId, cardId,
						CardStatFactory.newAction(DefaultCardStats.SHARED, 1L, aAction));
			} catch (final PersistException e) {
				throw new CompletionException(e);
			}
		});
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public DtoCard updateCard(final Principal aPrincipal, @RequestBody final DtoCard aCard)
			throws RestException {
		try {
			final String user = aPrincipal.getName();
			final Map<String, String> map = asMap(aCard.getContent());
			final Card card = cardManager.updateCard(user, UUID.fromString(aCard.getVocabularyId()),
					UUID.fromString(aCard.getId()), map);
			return DtoCodec.asDtoCard(card);
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/{voc_id}/{id}", method = RequestMethod.DELETE)
	public DtoVoid deleteCard(@PathVariable(value = "voc_id") final String aVocId,
			@PathVariable(value = "id") final String aId, final Principal aPrincipal)
			throws RestException {
		try {
			final String user = aPrincipal.getName();
			final UUID vocId = UUID.fromString(aVocId);
			final UUID id = UUID.fromString(aId);
			final Card card = cardManager.getCard(user, vocId, id);
			cardManager.deleteCard(user, vocId, id);
			if (card != null) {
				updateSourceStats(user, card.getSource(), StatAction.DECREMENT);
			}
			return DtoVoid.INSTANCE;
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

}
