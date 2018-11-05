package ru.dantalian.copvac.persist.nitrite.managers;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.exceptions.NitriteException;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;

import ru.dantalian.copvac.persist.api.PersistException;
import ru.dantalian.copvac.persist.api.PersistLanguageManager;
import ru.dantalian.copvac.persist.api.model.Language;
import ru.dantalian.copvac.persist.impl.model.personal.PojoLanguage;
import ru.dantalian.copvac.persist.nitrite.hibernate.model.DbLanguage;

@Singleton
public class NitritePersistLanguageManager implements PersistLanguageManager {

	@Inject
	private Nitrite db;

	private ObjectRepository<DbLanguage> languageRep;

	@Inject
	public void init() {
		languageRep = db.getRepository(DbLanguage.class);
	}

	@Override
	public List<Language> listLanguages(final Optional<String> aName, final Optional<String> aCountry,
			final Optional<String> aVariant) throws PersistException {
		try {
			final List<ObjectFilter> filters = new LinkedList<>();
			if (aName.isPresent()) {
				filters.add(ObjectFilters.eq("name", aName.get()));
			}
			if (aCountry.isPresent()) {
				filters.add(ObjectFilters.eq("country", aCountry.get()));
			}
			if (aVariant.isPresent()) {
				filters.add(ObjectFilters.eq("variant", aVariant.get()));
			}
			final ObjectFilter rootFilter = filters.isEmpty()
					? ObjectFilters.ALL : ObjectFilters.and(filters.toArray(new ObjectFilter[filters.size()]));
			final List<DbLanguage> langs = languageRep.find(rootFilter).toList();
			return langs.stream()
					.map(this::toLanguage)
					.collect(Collectors.toList());
		} catch (final NitriteException e) {
			throw new PersistException("Failed list languages", e);
		}
	}

	@Override
	public Language getLanguage(final String aName, final String aCountry, final String aVariant) throws PersistException {
		try {
			final DbLanguage lang = languageRep.find(ObjectFilters.and(
					ObjectFilters.eq("name", aName),
					ObjectFilters.eq("country", aCountry),
					ObjectFilters.eq("variant", aVariant)))
				.firstOrDefault();
			return toLanguage(lang);
		} catch (final NitriteException e) {
			throw new PersistException("Failed get a language", e);
		}
	}

	@Override
	public Language createLanguage(final String aName, final String aCountry, final String aVariant, final String aText)
			throws PersistException {
		try {
			final DbLanguage lang = new DbLanguage(aName, aCountry, aVariant, aText);
			languageRep.insert(lang);
			return toLanguage(lang);
		} catch (final NitriteException e) {
			throw new PersistException("Failed create a language", e);
		}
	}

	private Language toLanguage(final DbLanguage aLang) {
		return new PojoLanguage(aLang.getName(), aLang.getCountry(), aLang.getVariant(), aLang.getText());
	}

}
