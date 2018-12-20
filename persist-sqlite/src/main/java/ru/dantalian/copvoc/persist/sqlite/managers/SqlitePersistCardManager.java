package ru.dantalian.copvoc.persist.sqlite.managers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import ru.dantalian.copvoc.persist.api.PersistCardManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardFieldContent;
import ru.dantalian.copvoc.persist.api.query.CardsQuery;
import ru.dantalian.copvoc.persist.impl.model.PojoCard;
import ru.dantalian.copvoc.persist.impl.model.PojoCardFieldContent;
import ru.dantalian.copvoc.persist.sqlite.model.DbCard;
import ru.dantalian.copvoc.persist.sqlite.model.mappers.DbCardMapper;

@Service
public class SqlitePersistCardManager implements PersistCardManager {

	@Autowired
	private JdbcTemplate db;

	@Autowired
	private DbCardMapper mapper;

	@Override
	public Card createCard(final UUID aBatchId, final Map<String, String> aContent) throws PersistException {
		try {
			final UUID id = UUID.randomUUID();
			db.update("INSERT INTO card (id, batch_id, content) "
					+ " VALUES (?, ?, ?)",
					id.toString(),
					aBatchId.toString(),
					mapper.serializeMap(aContent));
			return asCard(new DbCard(id, aBatchId, aContent));
		} catch (final DataAccessException | IOException e) {
			throw new PersistException("Failed to create a card", e);
		}
	}

	@Override
	public void updateCard(final UUID aId, final Map<String, String> aContent) throws PersistException {
		try {
			final String id = aId.toString();
			final String content = mapper.serializeMap(aContent);
			db.update("UPDATE card SET content = ?"
					+ " WHERE id = ?",
					content,
					id);
		} catch (final DataAccessException | IOException e) {
			throw new PersistException("Failed to update a Card", e);
		}
	}

	@Override
	public Card getCard(final UUID aId) throws PersistException {
		try {
			final List<DbCard> list = db.query("select * from card "
					+ " WHERE id = ?",
					new Object[] {
							aId.toString(),
					},
					mapper);
			return asCard(CollectionUtils.lastElement(list));
		} catch (final DataAccessException e) {
			throw new PersistException("Failed to get card by id: " + aId, e);
		}
	}

	@Override
	public void deleteCard(final UUID aId) throws PersistException {
		try {
			db.update("DELETE FROM card "
					+ " WHERE id = ?"
					+ " AND name = ?",
					aId.toString());
		} catch (final DataAccessException e) {
			throw new PersistException("Failed to delete a card id: " + aId, e);
		}
	}

	@Override
	public List<Card> queryCards(final CardsQuery aQuery) throws PersistException {
		try {
			final List<DbCard> list = db.query("select * from card "
					+ " WHERE batch_id = ?",
					new Object[] {
							aQuery.getBatchId().toString()
					},
					mapper);
			return list.stream()
					.map(this::asCard)
					.collect(Collectors.toList());
		} catch (final DataAccessException e) {
			throw new PersistException("Failed to query cards", e);
		}
	}

	private Card asCard(final DbCard aDbCard) {
		final Map<String, String> fieldsContent = aDbCard.getFieldsContent();
		final Map<String, CardFieldContent> map = new HashMap<>();
		for (final Entry<String, String> entry: fieldsContent.entrySet()) {
			map.put(entry.getKey(), new PojoCardFieldContent(aDbCard.getId(), aDbCard.getBatchId(),
					entry.getKey(), entry.getValue()));
		}
		return new PojoCard(aDbCard.getId(), aDbCard.getBatchId(), map);
	}

}
