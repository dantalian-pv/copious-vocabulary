package ru.dantalian.copvoc.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import ru.dantalian.copvoc.core.CoreConstants;
import ru.dantalian.copvoc.core.CoreException;
import ru.dantalian.copvoc.core.model.DefaultField;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.Language;
import ru.dantalian.copvoc.persist.api.model.Vocabulary;
import ru.dantalian.copvoc.persist.api.utils.LanguageUtils;
import ru.dantalian.copvoc.persist.impl.model.PojoCardField;

@Service
public class FieldUtils {

	private ObjectMapper om;

	@PostConstruct
	public void init() {
		om = new ObjectMapper();
	}

	public List<CardField> getDefaultFields(final Vocabulary aVocabulary) throws CoreException {
		return getFields(aVocabulary.getId(), CoreConstants.DEFAULT_CARD_FIELDS);
	}

	public List<CardField> getLanguageFields(final UUID aVocabularyId, final Language aLanguage) throws CoreException {
		final String language = LanguageUtils.asString(aLanguage);
		return getFields(aVocabularyId, language + "_fields.json");
	}

	private List<CardField> getFields(final UUID aVocabularyId, final String aFileName) throws CoreException {
		final InputStream fieldsStream = this.getClass().getClassLoader()
				.getResourceAsStream(aFileName);
		if (fieldsStream == null) {
			return Collections.emptyList();
		}
		try {
			try {
				final ArrayNode arr = (ArrayNode) om.readTree(fieldsStream).get("fields");
				final List<CardField> fields = new LinkedList<>();
				for (final JsonNode node: arr) {
					final DefaultField field = om.treeToValue(node, DefaultField.class);
					fields.add(new PojoCardField(aVocabularyId, field.getName(), field.getType()));
				}
				return fields;
			} finally {
				fieldsStream.close();
			}
		} catch (final IOException e) {
			throw new CoreException("Failed to init fields", e);
		}
	}

}
