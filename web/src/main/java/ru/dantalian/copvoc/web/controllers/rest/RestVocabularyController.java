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

import ru.dantalian.copvoc.core.CoreException;
import ru.dantalian.copvoc.core.utils.FieldUtils;
import ru.dantalian.copvoc.core.utils.VocabularyUtils;
import ru.dantalian.copvoc.persist.api.PersistCardFieldManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistVocabularyManager;
import ru.dantalian.copvoc.persist.api.PersistVocabularyViewManager;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.Vocabulary;
import ru.dantalian.copvoc.persist.api.model.VocabularyView;
import ru.dantalian.copvoc.web.controllers.BadUserRequestException;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoVocabulary;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoVoid;
import ru.dantalian.copvoc.web.utils.DtoCodec;

@RestController
@RequestMapping(value = "/v1/api/vocabularies", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestVocabularyController {

	@Autowired
	private PersistVocabularyManager vocPersist;

	@Autowired
	private PersistVocabularyViewManager cardViewPersist;

	@Autowired
	private PersistCardFieldManager fieldManager;

	@Autowired
	private FieldUtils fieldUtils;

	@Autowired
	private VocabularyUtils vocUtils;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<DtoVocabulary> listVocs(final Principal aPrincipal) throws RestException {
		try {
		final String user = aPrincipal.getName();
		return vocPersist.listVocabularies(user)
				.stream()
				.map(DtoCodec::asDtoVocabulary)
				.collect(Collectors.toList());
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public DtoVocabulary getVoc(final Principal aPrincipal, @PathVariable(value = "id") final String id)
			throws RestException {
		try {
			final String user = aPrincipal.getName();
			final Vocabulary voc = vocPersist.getVocabulary(user, UUID.fromString(id));
			if (voc == null) {
				throw new PersistException("Vocabulary with id: " + id + " not found");
			}
			return DtoCodec.asDtoVocabulary(voc);
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public DtoVocabulary createVoc(final Principal aPrincipal, @RequestBody final DtoVocabulary aVocabulary)
			throws RestException {
		try {
			final String user = aPrincipal.getName();
			final Vocabulary queryVoc = vocPersist.queryVocabulary(user, aVocabulary.getName());
			if (queryVoc != null) {
				throw new BadUserRequestException("Vocabulary with given name already exists");
			}
			final Vocabulary voc = vocPersist.createVocabulary(user, aVocabulary.getName(),
					aVocabulary.getDescription(),
					DtoCodec.asLanguage(aVocabulary.getSourceId()),
					DtoCodec.asLanguage(aVocabulary.getTargetId()));
			// Init default fields
			final List<CardField> defaultFields = fieldUtils.getDefaultFields(voc);
			for (final CardField field: defaultFields) {
				fieldManager.createField(user, voc.getId(), field.getName(), field.getType());
			}
			// Init target language specific fields
			final List<CardField> targetLangFields = fieldUtils.getLanguageFields(voc.getId(), voc.getTarget());
			for (final CardField field: targetLangFields) {
				fieldManager.createField(user, voc.getId(), field.getName(), field.getType());
			}
			// Init source language specific fields
			final List<CardField> sourceLangFields = fieldUtils.getLanguageFields(voc.getId(), voc.getSource());
			for (final CardField field: sourceLangFields) {
				fieldManager.createField(user, voc.getId(), field.getName(), field.getType());
			}

			// Init default view
			final VocabularyView vocView = vocUtils.getDefaultView(voc.getId(), sourceLangFields, targetLangFields);
			cardViewPersist.createVocabularyView(user, voc.getId(), vocView.getCss(), vocView.getFront(), vocView.getBack());
			return DtoCodec.asDtoVocabulary(voc);
		} catch (final PersistException | CoreException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public DtoVoid updateVoc(final Principal aPrincipal, @RequestBody final DtoVocabulary aVocabulary)
			throws RestException {
		try {
			final String user = aPrincipal.getName();
			vocPersist.updateVocabulary(user, DtoCodec.asVocabulary(user, aVocabulary));
			return new DtoVoid();
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

}
