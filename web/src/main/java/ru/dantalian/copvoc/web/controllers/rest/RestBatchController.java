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
import ru.dantalian.copvoc.core.utils.BatchUtils;
import ru.dantalian.copvoc.core.utils.FieldUtils;
import ru.dantalian.copvoc.persist.api.PersistCardFieldManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistVocabularyManager;
import ru.dantalian.copvoc.persist.api.PersistVocabularyViewManager;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.Language;
import ru.dantalian.copvoc.persist.api.model.Vocabulary;
import ru.dantalian.copvoc.persist.api.model.VocabularyView;
import ru.dantalian.copvoc.persist.api.utils.LanguageUtils;
import ru.dantalian.copvoc.persist.impl.model.PojoVocabulary;
import ru.dantalian.copvoc.web.controllers.BadUserRequestException;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoVocabulary;

@RestController
@RequestMapping(value = "/v1/api/vocabularies", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestBatchController {

	@Autowired
	private PersistVocabularyManager batchPersist;

	@Autowired
	private PersistVocabularyViewManager batchViewPersist;

	@Autowired
	private PersistCardFieldManager fieldManager;

	@Autowired
	private FieldUtils fieldUtils;

	@Autowired
	private BatchUtils batchUtils;

	@RequestMapping(method = RequestMethod.GET)
	public List<DtoVocabulary> listBatches(final Principal aPrincipal) throws PersistException {
		final String user = aPrincipal.getName();
		return batchPersist.listVocabularies(user)
				.stream()
				.map(this::asDtoVocabulary)
				.collect(Collectors.toList());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public DtoVocabulary getBatch(final Principal aPrincipal, @PathVariable(value = "id") final String id)
			throws PersistException {
		final String user = aPrincipal.getName();
		final Vocabulary cardBatch = batchPersist.getVocabulary(user, UUID.fromString(id));
		if (cardBatch == null) {
			throw new PersistException("Vocabulary with id: " + id + " not found");
		}
		return asDtoVocabulary(cardBatch);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public DtoVocabulary createBatch(final Principal aPrincipal, @RequestBody final DtoVocabulary aVocabulary)
			throws PersistException, CoreException {
		final String user = aPrincipal.getName();
		final Vocabulary queryBatch = batchPersist.queryVocabulary(user, aVocabulary.getName());
		if (queryBatch != null) {
			throw new BadUserRequestException("Vocabulary with given name already exists");
		}
		final Vocabulary voc = batchPersist.createVocabulary(user, aVocabulary.getName(), aVocabulary.getDescription(),
				asLanguage(aVocabulary.getSourceId()), asLanguage(aVocabulary.getTargetId()));
		// Init default fields
		final List<CardField> defaultFields = fieldUtils.getDefaultFields(voc.getId());
		for (final CardField field: defaultFields) {
			fieldManager.createField(user, voc.getId(), field.getName(), field.getType());
		}
		// Init default view
		final VocabularyView vocView = batchUtils.getDefaultView(voc.getId());
		batchViewPersist.createVocabularyView(user, voc.getId(), vocView.getCss(), vocView.getFront(), vocView.getBack());
		return asDtoVocabulary(voc);
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateBatch(final Principal aPrincipal, @RequestBody final DtoVocabulary aCardBatch)
			throws PersistException {
		final String user = aPrincipal.getName();
		batchPersist.updateVocabulary(user, asVocabulary(user, aCardBatch));
	}

	private DtoVocabulary asDtoVocabulary(final Vocabulary aVocabulary) {
		if (aVocabulary == null) {
			return null;
		}
		return new DtoVocabulary(aVocabulary.getId().toString(), aVocabulary.getName(), aVocabulary.getDescription(),
				LanguageUtils.asString(aVocabulary.getSource()), aVocabulary.getSource().getText(),
				LanguageUtils.asString(aVocabulary.getTarget()), aVocabulary.getTarget().getText());
	}

	private Language asLanguage(final String aLanguage) {
		return LanguageUtils.asLanguage(aLanguage);
	}

	private Vocabulary asVocabulary(final String aUser, final DtoVocabulary aDtoCardBatch) {
		return new PojoVocabulary(UUID.fromString(aDtoCardBatch.getId()), aDtoCardBatch.getName(),
				aDtoCardBatch.getDescription(), aUser,
				asLanguage(aDtoCardBatch.getSourceId()), asLanguage(aDtoCardBatch.getTargetId()));
	}

}
