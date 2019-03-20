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
import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFieldContent;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoCard;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoValidation;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoValidationResult;
import ru.dantalian.copvoc.web.utils.DtoCodec;

@RestController
@RequestMapping(value = "/v1/api/validate", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestValidationController {

	@Autowired
	private PersistCardManager cardManager;

	@Autowired
	private PersistCardFieldManager fieldManager;

	@RequestMapping(value = "/{voc_id}/{id}", method = RequestMethod.GET)
	public DtoCard getCard(@PathVariable(value = "voc_id") final String aVocId,
			@PathVariable(value = "id") final String aId, final Principal aPrincipal)
					throws RestException {
		try {
			final String user = aPrincipal.getName();
			final Card card = cardManager.getCard(user, UUID.fromString(aVocId), UUID.fromString(aId));

			if (card == null) {
				throw new PersistException("Card with id: " + aId + " not found");
			}
			final List<CardField> fields = fieldManager.listFields(user, UUID.fromString(aVocId));
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
			throw new RestException("No answer field found in " + aId);
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/{voc_id}/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public DtoValidationResult getCard(@PathVariable(value = "voc_id") final String aVocId,
			@PathVariable(value = "id") final String aId, final Principal aPrincipal,
			@RequestBody final DtoValidation aValidation)
					throws RestException {
		try {
			final String user = aPrincipal.getName();
			final Card card = cardManager.getCard(user, UUID.fromString(aVocId), UUID.fromString(aId));
			if (card == null) {
				throw new PersistException("Card with id: " + aId + " not found");
			}
			final List<CardField> fields = fieldManager.listFields(user, UUID.fromString(aVocId));
			final Optional<CardField> field = fields.stream()
					.filter(aItem -> aItem.getType() == CardFiledType.ANSWER)
					.findFirst();
			if (field.isPresent()) {
				final CardFieldContent content = card.getContent(field.get().getName());
				if (content != null && content.getContent().toLowerCase().contains(aValidation.getAnswer().toLowerCase())) {
					return new DtoValidationResult(true, "valid");
				} else {
					return new DtoValidationResult(false, "Not valid answer");
				}
			}
			throw new RestException("No answer field found in " + aId);
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

}
