package ru.dantalian.copvoc.persist.sqlite.managers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import ru.dantalian.copvoc.persist.api.PersistCardFieldManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;
import ru.dantalian.copvoc.persist.impl.model.personal.PojoCardField;
import ru.dantalian.copvoc.persist.sqlite.model.DbCardField;
import ru.dantalian.copvoc.persist.sqlite.model.mappers.DbCardFieldMapper;

@Service
public class SqlitePersistCardFieldManager implements PersistCardFieldManager {

	@Autowired
	private JdbcTemplate db;

	@Autowired
	private DbCardFieldMapper mapper;

	@Override
	public CardField createField(final UUID aBatchId, final String aName, final String aDisplayName,
			final CardFiledType aType) throws PersistException {
		try {
			final UUID id = UUID.randomUUID();
			db.update("INSERT INTO card_field (id, batch_id, name, display_name, \"type\") VALUES (?, ?, ? ,?, ?)",
					id.toString(), aBatchId.toString(), aName, aDisplayName, aType.name());
			return toCardField(new DbCardField(id, aBatchId, aName, aDisplayName, aType));
		} catch (final DataAccessException e) {
			throw new PersistException("Failed to create a field", e);
		}
	}

	@Override
	public CardField updateField(final UUID aId, final String aDisplayName) throws PersistException {
		try {
			db.update("UPDATE card_field SET display_name = ?"
					+ " WHERE id = ?",
					aDisplayName,
					aId);
			return getField(aId);
		} catch (final DataAccessException e) {
			throw new PersistException("Failed to update a field", e);
		}
	}

	@Override
	public CardField getField(final UUID aId) throws PersistException {
		try {
			final List<DbCardField> list = db.query("select * from card_field where id = ?",
					new Object[] {
							aId.toString()
					},
					mapper);
			return toCardField(CollectionUtils.lastElement(list));
		} catch (final DataAccessException e) {
			throw new PersistException("Failed to get a field by id " + aId, e);
		}
	}

	@Override
	public void deleteField(final UUID aId) throws PersistException {
		try {
			db.update("DELETE FROM card_field "
					+ " WHERE id = ?",
					aId);
		} catch (final DataAccessException e) {
			throw new PersistException("Failed to delete a field", e);
		}
	}

	@Override
	public List<CardField> listFields(final UUID aBatchId) throws PersistException {
		try {
			final List<DbCardField> list = db.query("select * from card_field where batch_id = ?",
					new Object[] {
							aBatchId.toString()
					},
					mapper);
			return list.stream()
					.map(this::toCardField)
					.collect(Collectors.toList());
		} catch (final DataAccessException e) {
			throw new PersistException("Failed to list fields for batchId " + aBatchId, e);
		}
	}

	private CardField toCardField(final DbCardField aDbCardField) {
		return new PojoCardField(aDbCardField.getId(), aDbCardField.getBatchId(), aDbCardField.getName(),
				aDbCardField.getDisplayName(), aDbCardField.getType());
	}

}
