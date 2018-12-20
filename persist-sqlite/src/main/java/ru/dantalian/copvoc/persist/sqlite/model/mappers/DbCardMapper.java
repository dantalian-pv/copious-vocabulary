package ru.dantalian.copvoc.persist.sqlite.model.mappers;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.dantalian.copvoc.persist.api.model.CardFieldContent;
import ru.dantalian.copvoc.persist.sqlite.model.DbCard;
import ru.dantalian.copvoc.persist.sqlite.model.JsonCardContent;

@Service
public class DbCardMapper implements RowMapper<DbCard> {

	private ObjectMapper json;

	@PostConstruct
	public void init() {
		json = new ObjectMapper();
	}

	@Override
	public DbCard mapRow(final ResultSet aRs, final int aRowNum) throws SQLException {
		try {
			final UUID id = UUID.fromString(aRs.getString("id"));
			final UUID batchId = UUID.fromString(aRs.getString("batch_id"));
			final List<JsonCardContent> content = json.readValue(aRs.getString("content"),
					new TypeReference<List<JsonCardContent>>(){});
			final Map<String, String> map = new HashMap<>();
			for (final JsonCardContent item: content) {
				map.put(item.getFieldName(), item.getContent());
			}
			return new DbCard(id, batchId, map);
		} catch (final IOException e) {
			throw new SQLException("Failed to map card", e);
		}
	}

	public String serializeMap(final Map<String, String> aContent) throws IOException {
		final List<JsonCardContent> items = new LinkedList<>();
		for (final Entry<String, String> entry: aContent.entrySet()) {
			items.add(new JsonCardContent(entry.getKey(), entry.getValue()));
		}
		return json.writeValueAsString(items);
	}

	public String serializeContent(final Map<String, CardFieldContent> aContent) throws IOException {
		final List<JsonCardContent> items = new LinkedList<>();
		for (final Entry<String, CardFieldContent> entry: aContent.entrySet()) {
			items.add(new JsonCardContent(entry.getKey(), entry.getValue().getContent()));
		}
		return json.writeValueAsString(items);
	}

}
