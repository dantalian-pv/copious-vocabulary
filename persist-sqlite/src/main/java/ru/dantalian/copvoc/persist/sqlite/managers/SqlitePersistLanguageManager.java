package ru.dantalian.copvoc.persist.sqlite.managers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistLanguageManager;
import ru.dantalian.copvoc.persist.api.model.Language;
import ru.dantalian.copvoc.persist.impl.model.PojoLanguage;
import ru.dantalian.copvoc.persist.sqlite.model.DbLanguage;
import ru.dantalian.copvoc.persist.sqlite.model.mappers.DbLanguageMapper;

@Service
public class SqlitePersistLanguageManager implements PersistLanguageManager {

	@Autowired
	private JdbcTemplate db;

	@Autowired
	private DbLanguageMapper mapper;

	@Override
	public List<Language> listLanguages(final Optional<String> aName, final Optional<String> aCountry,
			final Optional<String> aVariant) throws PersistException {
		try {
			final List<DbLanguage> langs;
			if (!aName.isPresent() && !aCountry.isPresent() && !aVariant.isPresent()) {
				langs = db.query("SELECT * FROM language", mapper);
			} else if (aName.isPresent() && !aCountry.isPresent() && !aVariant.isPresent()) {
				langs = db.query("SELECT * FROM language where"
						+ " name = ?"
						+ " AND country = ?",
						new Object[] {
								aName.get(),
								aName.get().toUpperCase()
						},
						mapper);
			} else if (aName.isPresent() && aCountry.isPresent() && !aVariant.isPresent()) {
				langs = db.query("SELECT * FROM language WHERE"
						+ " name = ?"
						+ " AND country = ?",
						new Object[] {
								aName.get(),
								aCountry.get()
						},
						mapper);
			} else {
				langs = db.query("SELECT * FROM language WHERE"
						+ " name = ?"
						+ " AND country = ?"
						+ " AND variant = ?",
						new Object[] {
							aName.get(),
							aCountry.get(),
							aVariant.get()
						},
						mapper);
			}
			return langs.stream()
			.map(this::toLanguage)
			.collect(Collectors.toList());
		} catch (final DataAccessException e) {
			throw new PersistException("Failed list languages", e);
		}
	}

	@Override
	public Language getLanguage(final String aName, final String aCountry, final String aVariant)
			throws PersistException {
		final List<Language> languages = listLanguages(Optional.ofNullable(aName),
				Optional.ofNullable(aCountry), Optional.ofNullable(aVariant));
		if (languages.iterator().hasNext()) {
			return languages.iterator().next();
		}
		return null;
	}

	@Override
	public Language createLanguage(final String aName, final String aCountry, final String aVariant,
			final String aText) throws PersistException {
		try {
			db.update("INSERT INTO language (name, country, variant, text) VALUES (?, ?, ? ,?)",
					aName, aCountry, aVariant, aText);
			return toLanguage(new DbLanguage(aName, aCountry, aVariant, aText));
		} catch (final DataAccessException e) {
			throw new PersistException("Failed create a language", e);
		}
	}

	@Override
	public Language updateLanguage(final String aName, final String aCountry, final String aVariant,
			final String aText) throws PersistException {
		try {
			db.update("UPDATE language SET text = ?"
					+ " WHERE name = ?"
					+ " AND country = ?"
					+ " AND variant = ?",
					aText,
					aName,
					aCountry,
					aVariant);
			return toLanguage(new DbLanguage(aName, aCountry, aVariant, aText));
		} catch (final DataAccessException e) {
			throw new PersistException("Failed create a language", e);
		}
	}

	private Language toLanguage(final DbLanguage aLang) {
		return new PojoLanguage(aLang.getName(), aLang.getCountry(), aLang.getVariant(), aLang.getText());
	}

}
