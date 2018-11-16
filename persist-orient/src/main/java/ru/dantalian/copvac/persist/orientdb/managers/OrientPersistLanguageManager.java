package ru.dantalian.copvac.persist.orientdb.managers;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.exception.OCommandExecutionException;
import com.orientechnologies.orient.core.sql.OCommandSQLParsingException;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.object.db.OrientDBObject;

import ru.dantalian.copvac.persist.api.PersistException;
import ru.dantalian.copvac.persist.api.PersistLanguageManager;
import ru.dantalian.copvac.persist.api.model.Language;
import ru.dantalian.copvac.persist.impl.model.personal.PojoLanguage;
import ru.dantalian.copvac.persist.orientdb.model.DbLanguage;

@Singleton
public class OrientPersistLanguageManager implements PersistLanguageManager {

	@Inject
	private ODatabaseObject session;

	@Inject
	private OrientDBObject db;

	@Override
	public List<Language> listLanguages(final Optional<String> aName, final Optional<String> aCountry,
			final Optional<String> aVariant) throws PersistException {
		try {
			final OResultSet langs = session.query("select * from DbLanguage where name = ? "
					+ "and country = ? "
					+ "and variant = ?",
					aName.orElse(""),
					aCountry.orElse(""),
					aVariant.orElse(""));
			return langs.stream()
			.map(aItem -> this.toLanguage(aItem.toElement().getRecord()))
			.collect(Collectors.toList());
		} catch (final OCommandSQLParsingException | OCommandExecutionException e) {
			throw new PersistException("Failed list languages", e);
		}
	}

	@Override
	public Language getLanguage(final String aName, final String aCountry, final String aVariant)
			throws PersistException {
		final List<Language> languages = listLanguages(Optional.of(aName), Optional.of(aCountry), Optional.of(aVariant));
		if (languages.iterator().hasNext()) {
			return languages.iterator().next();
		}
		return null;
	}

	@Override
	public Language createLanguage(final String aName, final String aCountry, final String aVariant,
			final String aText) throws PersistException {
		try {
			final DbLanguage lang = new DbLanguage(aName, aCountry, aVariant, aText);
			session.save(lang);
			return toLanguage(lang);
		} catch (final OCommandSQLParsingException | OCommandExecutionException e) {
			throw new PersistException("Failed create a language", e);
		}
	}

	@Override
	public Language updateLanguage(final String aName, final String aCountry, final String aVariant,
			final String aText) throws PersistException {
		try {
			final DbLanguage lang = new DbLanguage(aName, aCountry, aVariant, aText);
			session.save(lang);
			return toLanguage(lang);
		} catch (final OCommandSQLParsingException | OCommandExecutionException e) {
			throw new PersistException("Failed create a language", e);
		}
	}

	private Language toLanguage(final DbLanguage aLang) {
		return new PojoLanguage(aLang.getId().getName(), aLang.getId().getCountry(), aLang.getVariant(), aLang.getText());
	}

	@Override
	public void close() throws IOException {
		this.session.close();
		this.db.close();
	}

}
