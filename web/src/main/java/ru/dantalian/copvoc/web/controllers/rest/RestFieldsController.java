package ru.dantalian.copvoc.web.controllers.rest;

import java.security.Principal;
import java.util.List;
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

import ru.dantalian.copvoc.persist.api.PersistCardFieldManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistVocabularyManager;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;
import ru.dantalian.copvoc.persist.api.model.Vocabulary;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoField;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoVoid;
import ru.dantalian.copvoc.web.utils.DtoCodec;

@RestController
@RequestMapping(value = "/v1/api/fields", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestFieldsController {

	@Autowired
	private PersistVocabularyManager vocManager;

	@Autowired
	private PersistCardFieldManager fieldManager;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public List<DtoField> listFields(final Principal aPrincipal,
			@PathVariable(value = "id") final String aId) throws RestException {
		try {
		final String user = aPrincipal.getName();
		final Vocabulary voc = vocManager.getVocabulary(user, UUID.fromString(aId));
		if (voc == null) {
			throw new PersistException("Vocabulary with id: " + aId + " not found");
		}
		return fieldManager.listFields(user, UUID.fromString(aId))
				.stream()
				.map(DtoCodec::asDtoField)
				.collect(Collectors.toList());
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/{id}/{name}", method = RequestMethod.GET)
	@ResponseBody
	public DtoField getField(final Principal aPrincipal, @PathVariable(value = "id") final String aId,
			@PathVariable(value = "name") final String aName) throws RestException {
		try {
			final String user = aPrincipal.getName();
			final Vocabulary voc = vocManager.getVocabulary(user, UUID.fromString(aId));
			if (voc == null) {
				throw new PersistException("Vocabulary with id: " + aId + " not found");
			}
			final CardField field = fieldManager.getField(user, UUID.fromString(aId), aName);
			return DtoCodec.asDtoField(field);
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public DtoField createField(final Principal aPrincipal, @RequestBody final DtoField aDtoField)
			throws RestException {
		try {
			final String user = aPrincipal.getName();
			final Vocabulary voc = vocManager.getVocabulary(user, UUID.fromString(aDtoField.getVocabularyId()));
			if (voc == null) {
				throw new PersistException("Vocabulary with id: " + aDtoField.getVocabularyId() + " not found");
			}
			final CardField field = fieldManager.getField(user,
					UUID.fromString(aDtoField.getVocabularyId()), aDtoField.getName());
			if (field != null) {
				throw new RestException("Field with a given name already exists");
			}
			final List<CardField> fields = fieldManager.listFields(user, voc.getId())
					.stream()
					.filter(aItem -> aItem.getOrder() < 1000)
					.collect(Collectors.toList());
			final CardField lastField = fields.get(fields.size() - 1);

			final CardField createdField = fieldManager.createField(user, UUID.fromString(aDtoField.getVocabularyId()),
					aDtoField.getName(), CardFiledType.valueOf(aDtoField.getType()),
					lastField.getOrder() + 1, false);
			return DtoCodec.asDtoField(createdField);
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/{id}/{name}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public DtoVoid deleteField(final Principal aPrincipal,
			@PathVariable(value = "id") final String aId,
			@PathVariable(value = "name") final String aName) throws RestException {
		try {
			final String user = aPrincipal.getName();
			final Vocabulary voc = vocManager.getVocabulary(user, UUID.fromString(aId));
			if (voc == null) {
				throw new PersistException("Vocabulary with id: " + aId + " not found");
			}
			fieldManager.deleteField(user, UUID.fromString(aId), aName);
			return DtoVoid.INSTANCE;
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

}
