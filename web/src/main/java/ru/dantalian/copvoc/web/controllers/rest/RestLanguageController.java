package ru.dantalian.copvoc.web.controllers.rest;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistLanguageManager;
import ru.dantalian.copvoc.persist.api.model.Language;
import ru.dantalian.copvoc.persist.api.utils.LanguageUtils;
import ru.dantalian.copvoc.web.controllers.rest.model.DtoLanguage;

@RestController
@RequestMapping(value = "/v1/api/langs", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestLanguageController {

	@Autowired
	private PersistLanguageManager mLangManager;

	@RequestMapping(method = RequestMethod.GET)
	public List<DtoLanguage> listLanguages(final Principal aPrincipal,
			@RequestParam(value = "name", required = false) final String aName,
			@RequestParam(value = "country", required = false) final String aCountry,
			@RequestParam(value = "variant", required = false) final String aVariant) throws RestException {
		try {
			return mLangManager.listLanguages(Optional.ofNullable(aName),
					Optional.ofNullable(aCountry), Optional.ofNullable(aVariant))
					.stream()
					.map(this::asDtoLanguage)
					.collect(Collectors.toList());
		} catch (final PersistException e) {
			throw new RestException(e.getMessage(), e);
		}
	}

	private DtoLanguage asDtoLanguage(final Language aLanguage) {
		if (aLanguage == null) {
			return null;
		}
		return new DtoLanguage(LanguageUtils.asString(aLanguage), aLanguage.getName(), aLanguage.getCountry(),
				aLanguage.getVariant(), aLanguage.getText());
	}

}
