package ru.dantalian.copvac.persist.api;

import java.util.List;
import java.util.Optional;

import ru.dantalian.copvac.persist.api.model.Language;

public interface PersistLanguageManager {

	List<Language> listLanguages(final Optional<String> aName,
		final Optional<String> aCountry, final Optional<String> aVariant)
			throws PersistException;

	Language getLanguage(final String aName, final String aCountry, final String aVariant)
			throws PersistException;

	Language createLanguage(final String aName, final String aCountry, final String aVariant,
			final String aText) throws PersistException;

}
