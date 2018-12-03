package ru.dantalian.copvoc.persist.api;

import java.io.Closeable;
import java.util.List;
import java.util.Optional;

import ru.dantalian.copvoc.persist.api.model.Language;

public interface PersistLanguageManager extends Closeable {

	List<Language> listLanguages(final Optional<String> aName,
		final Optional<String> aCountry, final Optional<String> aVariant)
			throws PersistException;

	Language getLanguage(final String aName, final String aCountry, final String aVariant)
			throws PersistException;

	Language createLanguage(final String aName, final String aCountry, final String aVariant,
			final String aText) throws PersistException;

	Language updateLanguage(final String aName, final String aCountry, final String aVariant,
			final String aText) throws PersistException;

}
