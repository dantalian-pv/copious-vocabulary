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

import ru.dantalian.copvoc.persist.api.PersistBatchManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.CardBatch;
import ru.dantalian.copvoc.persist.api.model.Language;
import ru.dantalian.copvoc.persist.api.utils.LanguageUtils;
import ru.dantalian.copvoc.persist.impl.model.personal.PojoCardBatch;
import ru.dantalian.copvoc.web.controllers.BadUserRequestException;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoCardBatch;

@RestController
@RequestMapping(value = "/v1/api/batches", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestBatchController {

	@Autowired
	private PersistBatchManager mBatchPersist;

	@RequestMapping(method = RequestMethod.GET)
	public List<DtoCardBatch> listBatches(final Principal aPrincipal) throws PersistException {
		final String user = aPrincipal.getName();
		return mBatchPersist.listBatches(user)
				.stream()
				.map(this::asDtoCardBatch)
				.collect(Collectors.toList());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public DtoCardBatch getBatch(final Principal aPrincipal, @PathVariable(value = "id") final String id)
			throws PersistException {
		final String user = aPrincipal.getName();
		final CardBatch cardBatch = mBatchPersist.getBatch(user, UUID.fromString(id));
		if (cardBatch == null) {
			throw new PersistException("CardBatch with id: " + id + " not found");
		}
		return asDtoCardBatch(cardBatch);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public DtoCardBatch createBatch(final Principal aPrincipal, @RequestBody final DtoCardBatch aCardBatch)
			throws PersistException {
		final String user = aPrincipal.getName();
		final CardBatch queryBatch = mBatchPersist.queryBatch(user, aCardBatch.getName());
		if (queryBatch != null) {
			throw new BadUserRequestException("CardBatch with given name already exists");
		}
		final CardBatch batch = mBatchPersist.createBatch(user, aCardBatch.getName(), aCardBatch.getDescription(),
				asLanguage(aCardBatch.getSourceId()), asLanguage(aCardBatch.getTargetId()));
		return asDtoCardBatch(batch);
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateBatch(final Principal aPrincipal, @RequestBody final DtoCardBatch aCardBatch)
			throws PersistException {
		final String user = aPrincipal.getName();
		mBatchPersist.updateBatch(user, asCardBatch(user, aCardBatch));
	}

	private DtoCardBatch asDtoCardBatch(final CardBatch aCardBatch) {
		if (aCardBatch == null) {
			return null;
		}
		return new DtoCardBatch(aCardBatch.getId().toString(), aCardBatch.getName(), aCardBatch.getDescription(),
				LanguageUtils.asString(aCardBatch.getSource()), aCardBatch.getSource().getText(),
				LanguageUtils.asString(aCardBatch.getTarget()), aCardBatch.getTarget().getText());
	}

	private Language asLanguage(final String aLanguage) {
		return LanguageUtils.asLanguage(aLanguage);
	}

	private CardBatch asCardBatch(final String aUser, final DtoCardBatch aDtoCardBatch) {
		return new PojoCardBatch(UUID.fromString(aDtoCardBatch.getId()), aDtoCardBatch.getName(),
				aDtoCardBatch.getDescription(), aUser,
				asLanguage(aDtoCardBatch.getSourceId()), asLanguage(aDtoCardBatch.getTargetId()));
	}

}
