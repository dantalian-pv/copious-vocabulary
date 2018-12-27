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
import ru.dantalian.copvoc.core.utils.VocabularyUtils;
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
	public List<DtoVocabulary> listVocs(final Principal aPrincipal) throws PersistException {
		final String user = aPrincipal.getName();
		return vocPersist.listVocabularies(user)
				.stream()
				.map(this::asDtoVocabulary)
				.collect(Collectors.toList());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public DtoVocabulary getVoc(final Principal aPrincipal, @PathVariable(value = "id") final String id)
			throws PersistException {
		final String user = aPrincipal.getName();
		final Vocabulary voc = vocPersist.getVocabulary(user, UUID.fromString(id));
		if (voc == null) {
			throw new PersistException("Vocabulary with id: " + id + " not found");
		}
		return asDtoVocabulary(voc);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public DtoVocabulary createVoc(final Principal aPrincipal, @RequestBody final DtoVocabulary aVocabulary)
			throws PersistException, CoreException {
		final String user = aPrincipal.getName();
		final Vocabulary queryVoc = vocPersist.queryVocabulary(user, aVocabulary.getName());
		if (queryVoc != null) {
			throw new BadUserRequestException("Vocabulary with given name already exists");
		}
		final Vocabulary voc = vocPersist.createVocabulary(user, aVocabulary.getName(), aVocabulary.getDescription(),
				asLanguage(aVocabulary.getSourceId()), asLanguage(aVocabulary.getTargetId()));
		// Init default fields
		final List<CardField> defaultFields = fieldUtils.getDefaultFields(voc.getId());
		for (final CardField field: defaultFields) {
			fieldManager.createField(user, voc.getId(), field.getName(), field.getType());
		}
		// Init default view
		final VocabularyView vocView = vocUtils.getDefaultView(voc.getId());
		cardViewPersist.createVocabularyView(user, voc.getId(), vocView.getCss(), vocView.getFront(), vocView.getBack());
		return asDtoVocabulary(voc);
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateVoc(final Principal aPrincipal, @RequestBody final DtoVocabulary aVocabulary)
			throws PersistException {
		final String user = aPrincipal.getName();
		vocPersist.updateVocabulary(user, asVocabulary(user, aVocabulary));
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

	private Vocabulary asVocabulary(final String aUser, final DtoVocabulary aDtoVocabulary) {
		return new PojoVocabulary(UUID.fromString(aDtoVocabulary.getId()), aDtoVocabulary.getName(),
				aDtoVocabulary.getDescription(), aUser,
				asLanguage(aDtoVocabulary.getSourceId()), asLanguage(aDtoVocabulary.getTargetId()));
	}

}
