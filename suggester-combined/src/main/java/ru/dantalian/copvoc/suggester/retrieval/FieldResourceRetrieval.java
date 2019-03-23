package ru.dantalian.copvoc.suggester.retrieval;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import ru.dantalian.copvoc.persist.api.PersistCardFieldManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.suggester.api.SuggestException;
import ru.dantalian.copvoc.suggester.api.UniversalRetrieval;

@Component("field")
@Order(10)
public class FieldResourceRetrieval implements UniversalRetrieval {

	@Autowired
	private PersistCardFieldManager fieldManager;

	@Override
	public boolean accept(final URI aSource) {
		final String scheme = aSource.getScheme();
		return "field".equals(scheme);
	}

	@Override
	public Map<String, Object> retrieve(final String aUser, final URI aSource) throws SuggestException {
		try {
			final UUID vocId = UUID.fromString(aSource.getHost());
			final String name = aSource.getPath().replace("/", "");
			if ("__all__".equals(name)) {
				final List<CardField> fields = fieldManager.listFields(aUser, vocId);
				final Map<String, Object> map = new HashMap<>();
				for (final CardField field: fields) {
					map.put(field.getName(), field.getType().name());
				}
				return map;
			} else {
				final CardField field = fieldManager.getField(aUser, vocId, name);
				if (field == null) {
					return Collections.emptyMap();
				}
				return Collections.singletonMap(field.getName(), field.getType().name());
			}
		} catch (final PersistException e) {
			throw new SuggestException("Failed to retrieve " + aSource, e);
		}
	}

}
