package ru.dantalian.copvoc.web.controllers.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import ru.dantalian.copvoc.persist.api.PersistCardManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistVocabularyManager;
import ru.dantalian.copvoc.persist.api.PersistVocabularyViewManager;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.Vocabulary;
import ru.dantalian.copvoc.persist.api.model.VocabularyView;
import ru.dantalian.copvoc.persist.api.utils.LanguageUtils;
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
	private PersistVocabularyViewManager viewPersist;

	@Autowired
	private PersistCardFieldManager fieldManager;

	@Autowired
	private PersistCardManager cardsManager;

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
	public DtoVocabulary getVoc(final Principal aPrincipal, @PathVariable(value = "id") final String aId)
			throws RestException {
		try {
			final String user = aPrincipal.getName();
			final Vocabulary voc = vocPersist.getVocabulary(user, UUID.fromString(aId));
			if (voc == null) {
				throw new PersistException("Vocabulary with id: " + aId + " not found");
			}
			return DtoCodec.asDtoVocabulary(voc);
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/{id}/_export", method = RequestMethod.GET)
	public void exportVoc(final Principal aPrincipal,
			@PathVariable(value = "id") final String aId,
			final HttpServletResponse response) throws RestException {
		try(OutputStream stream = response.getOutputStream()) {
			final String user = aPrincipal.getName();
			final Vocabulary voc = vocPersist.getVocabulary(user, UUID.fromString(aId));
			if (voc == null) {
				throw new PersistException("Vocabulary with id: " + aId + " not found");
			}
			final String vocName = voc.getName() + " (" + LanguageUtils.asString(voc.getSource()) + "->"
					+ LanguageUtils.asString(voc.getTarget()) + ") " + voc.getDescription() + ".json";
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + vocName + "\"");
			response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
			vocUtils.exportVocabulary(user, stream, voc.getId());
		} catch (final PersistException | IOException e) {
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
				fieldManager.createField(user, voc.getId(), field.getName(), field.getType(), field.getOrder(), field.isSystem());
			}
			// Init target language specific fields
			final List<CardField> targetLangFields = fieldUtils.getLanguageFields(voc.getId(), voc.getTarget());
			for (final CardField field: targetLangFields) {
				fieldManager.createField(user, voc.getId(), field.getName(), field.getType(), field.getOrder(), field.isSystem());
			}
			// Init default view
			final VocabularyView vocView = vocUtils.getDefaultView(voc.getId(), targetLangFields);
			viewPersist.createVocabularyView(user, voc.getId(), vocView.getCss(), vocView.getFront(), vocView.getBack());
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

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public DtoVoid deleteVoc(final Principal aPrincipal, @PathVariable(value = "id") final String aId)
			throws RestException {
		try {
			final String user = aPrincipal.getName();
			final UUID vocId = UUID.fromString(aId);
			final Vocabulary vocabulary = vocPersist.getVocabulary(user, vocId);
			if (!vocabulary.getUser().equals(user)) {
				throw new RestException("Operation is not allowed");
			}
			cardsManager.deleteAllCards(user, vocId);
			final List<CardField> fields = fieldManager.listFields(user, vocId);
			for (final CardField field: fields) {
				fieldManager.deleteField(user, vocId, field.getName());
			}
			viewPersist.deleteVocabularyView(user, vocId);
			vocPersist.deleteVocabulary(user, vocId);
			return new DtoVoid();
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

}
