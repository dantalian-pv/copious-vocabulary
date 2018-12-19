package ru.dantalian.copvoc.core.managers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import ru.dantalian.copvoc.core.CoreConstants;
import ru.dantalian.copvoc.core.model.DefaultField;
import ru.dantalian.copvoc.persist.api.PersistCardFieldManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;

@Service
public class FieldManager {

	@Autowired
	private PersistCardFieldManager persistFieldManager;

	public void initPredefinedFields(final UUID aBatchId) throws PersistException {
		final ObjectMapper om = new ObjectMapper();
		try (InputStream fieldsStream = this.getClass().getClassLoader()
				.getResourceAsStream(CoreConstants.DEFAULT_CARD_BATCH_FIELDS)) {
			final ArrayNode arr = (ArrayNode) om.readTree(fieldsStream).get("fields");
			for (final JsonNode node: arr) {
				final DefaultField field = om.treeToValue(node, DefaultField.class);
				persistFieldManager.createField(aBatchId, field.getName(), field.getType());
			}
		} catch (final IOException e) {
			throw new PersistException("Failed to init fields", e);
		}
	}

	public CardField createField(final UUID aBatchId, final String aName,
			final CardFiledType aType) throws PersistException {
		return persistFieldManager.createField(aBatchId, aName, aType);
	}

	public CardField getField(final UUID aId) throws PersistException {
		return persistFieldManager.getField(aId);
	}

	public void deleteField(final UUID aId) throws PersistException {
		persistFieldManager.deleteField(aId);
	}

	public List<CardField> listFields(final UUID aBatchId) throws PersistException {
		return persistFieldManager.listFields(aBatchId);
	}

}
