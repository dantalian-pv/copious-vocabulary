package ru.dantalian.copvoc.web.controllers.rest;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ru.dantalian.copvoc.persist.api.PersistCardFieldManager;
import ru.dantalian.copvoc.persist.api.PersistCardManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistTrainingManager;
import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFieldContent;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;
import ru.dantalian.copvoc.persist.api.model.Training;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoCard;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoTraining;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoTrainingResult;
import ru.dantalian.copvoc.web.utils.DtoCodec;

@RestController
@RequestMapping(value = "/v1/api/train", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestTrainingController {

	@Autowired
	private PersistCardManager cardManager;

	@Autowired
	private PersistTrainingManager trainingManager;

	@Autowired
	private PersistCardFieldManager fieldManager;

	@RequestMapping(value = "/{training_id}/{card_id}", method = RequestMethod.GET)
	public DtoCard getCard(@PathVariable(value = "training_id") final String aTrainingId,
			@PathVariable(value = "card_id") final String aCardId, final Principal aPrincipal)
					throws RestException {
		try {
			final String user = aPrincipal.getName();
			final UUID trainingId = UUID.fromString(aTrainingId);
			final Training training = trainingManager.getTraining(user, trainingId);
			if (training == null) {
				throw new PersistException("No training found");
			}
			final Card card = cardManager.getCard(user, training.getVocabularyId(), UUID.fromString(aCardId));

			if (card == null) {
				throw new PersistException("Card with id: " + aCardId + " not found");
			}
			final List<CardField> fields = fieldManager.listFields(user, training.getVocabularyId());
			final Optional<CardField> field = fields.stream()
					.filter(aItem -> aItem.getType() == CardFiledType.ANSWER)
					.findFirst();
			if (field.isPresent()) {
				final String answer = card.getContent(field.get().getName()).getContent();
				String word = card.getContent("word").getContent();
				word = word == null ? "" : word;
				// Replace in all text fields the answer
				for (final CardField fld: fields) {
					if (fld.getType() == CardFiledType.MARKUP || fld.getType() == CardFiledType.TEXT) {
						final CardFieldContent content = card.getContent(fld.getName());
						if (content != null && content.getContent() != null) {
							final String cnt = content.getContent().replaceAll("\\b"+answer+"\\b", word);
							content.setContent(cnt);
						}
					}
				}
				return DtoCodec.asDtoCard(card);
			}
			throw new RestException("No answer field found in " + aCardId);
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/{training_id}/{card_id}/_next", method = RequestMethod.GET)
	public DtoCard getNextCard(@PathVariable(value = "training_id") final String aTrainingId,
			@PathVariable(value = "card_id") final String aCardId, final Principal aPrincipal)
					throws RestException {
		try {
			final String user = aPrincipal.getName();
			final UUID cardId = UUID.fromString(aCardId);
			final UUID trainingId = UUID.fromString(aTrainingId);
			final Training training = trainingManager.getTraining(user, trainingId);
			if (training == null) {
				throw new PersistException("No training found");
			}
			final UUID nextCard = trainingManager.nextCard(user, trainingId, cardId);
			if (nextCard == null) {
				return new DtoCard();
			}
			return getCard(aTrainingId, nextCard.toString(), aPrincipal);
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/{training_id}/{card_id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public DtoTrainingResult checkCard(@PathVariable(value = "training_id") final String aTrainingId,
			@PathVariable(value = "card_id") final String aCardId, final Principal aPrincipal,
			@RequestBody final DtoTraining aValidation)
					throws RestException {
		try {
			final String user = aPrincipal.getName();
			final UUID trainingId = UUID.fromString(aTrainingId);
			final Training training = trainingManager.getTraining(user, trainingId);
			if (training == null) {
				throw new PersistException("No training found");
			}

			final Card card = cardManager.getCard(user, training.getVocabularyId(), UUID.fromString(aCardId));
			if (card == null) {
				throw new PersistException("Card with id: " + aCardId + " not found");
			}
			final List<CardField> fields = fieldManager.listFields(user, training.getVocabularyId());
			final Optional<CardField> field = fields.stream()
					.filter(aItem -> aItem.getType() == CardFiledType.ANSWER)
					.findFirst();
			if (field.isPresent()) {
				final CardFieldContent content = card.getContent(field.get().getName());
				if (content != null && content.getContent().toLowerCase().contains(aValidation.getAnswer().toLowerCase())) {
					return new DtoTrainingResult(true, "valid");
				} else {
					return new DtoTrainingResult(false, "Not valid answer");
				}
			}
			throw new RestException("No answer field found in " + aCardId);
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

}
