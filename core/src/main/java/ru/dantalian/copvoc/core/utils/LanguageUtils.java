package ru.dantalian.copvoc.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import ru.dantalian.copvoc.core.CoreConstants;
import ru.dantalian.copvoc.core.CoreException;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistLanguageManager;
import ru.dantalian.copvoc.persist.api.model.Language;
import ru.dantalian.copvoc.persist.impl.model.PojoLanguage;

@Service
public class LanguageUtils {

	@Autowired
	private PersistLanguageManager languageManager;

	public List<Language> getDefaultLanguages() throws CoreException {
		final ObjectMapper om = new ObjectMapper();
		try (InputStream langStream = this.getClass().getClassLoader()
				.getResourceAsStream(CoreConstants.DEFAULT_LANGUAGES)) {
			final ArrayNode arr = (ArrayNode) om.readTree(langStream).get("laguages");
			final List<Language> list = new LinkedList<>();
			for (final JsonNode node: arr) {
				final PojoLanguage lang = om.treeToValue(node, PojoLanguage.class);
				list.add(lang);
			}
			return list;
		} catch (final IOException e) {
			throw new CoreException("Failed to init languages", e);
		}
	}

	public void upsertLanguages(final List<Language> aList)
			throws PersistException {
		for (final Language lang: aList) {
			final Language storedLang = languageManager.getLanguage(lang.getName(), lang.getCountry(), lang.getVariant());
			if (storedLang == null) {
				languageManager.createLanguage(lang.getName(), lang.getCountry(), lang.getVariant(), lang.getText());
			}
		}
	}

}
