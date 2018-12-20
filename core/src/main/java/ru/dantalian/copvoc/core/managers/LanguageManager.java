package ru.dantalian.copvoc.core.managers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import ru.dantalian.copvoc.core.CoreConstants;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistLanguageManager;
import ru.dantalian.copvoc.persist.api.model.Language;
import ru.dantalian.copvoc.persist.impl.model.PojoLanguage;

@Service
public class LanguageManager implements PersistLanguageManager {

	@Autowired
	private PersistLanguageManager languagePersist;

	public List<Language> initLanguages() throws PersistException {
		final ObjectMapper om = new ObjectMapper();
		try (InputStream langStream = this.getClass().getClassLoader()
				.getResourceAsStream(CoreConstants.DEFAULT_LANGUAGES)) {
			final ArrayNode arr = (ArrayNode) om.readTree(langStream).get("laguages");
			for (final JsonNode node: arr) {
				final PojoLanguage lang = om.treeToValue(node, PojoLanguage.class);
				final Language persistLang = languagePersist.getLanguage(
						lang.getName(), lang.getCountry(), lang.getVariant());
				if (persistLang == null) {
					languagePersist.createLanguage(lang.getName(), lang.getCountry(), lang.getVariant(), lang.getText());
				}
			}
			return languagePersist.listLanguages(Optional.empty(), Optional.empty(), Optional.empty());
		} catch (final IOException e) {
			throw new PersistException("Failed to init languages", e);
		}
	}

	@Override
	public List<Language> listLanguages(final Optional<String> aName, final Optional<String> aCountry,
			final Optional<String> aVariant) throws PersistException {
		return languagePersist.listLanguages(aName, aCountry, aVariant);
	}

	@Override
	public Language getLanguage(final String aName, final String aCountry, final String aVariant)
			throws PersistException {
		return languagePersist.getLanguage(aName, aCountry, aVariant);
	}

	@Override
	public Language createLanguage(final String aName, final String aCountry, final String aVariant,
			final String aText) throws PersistException {
		return languagePersist.createLanguage(aName, aCountry, aVariant, aText);
	}

	@Override
	public Language updateLanguage(final String aName, final String aCountry, final String aVariant, final String aText)
			throws PersistException {
		return languagePersist.updateLanguage(aName, aCountry, aVariant, aText);
	}

}
