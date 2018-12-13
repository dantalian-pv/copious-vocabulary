package ru.dantalian.copvoc.persist.sqlite.managers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import ru.dantalian.copvoc.persist.api.PersistBatchManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.CardBatch;
import ru.dantalian.copvoc.persist.api.model.Language;
import ru.dantalian.copvoc.persist.api.utils.LanguageUtils;
import ru.dantalian.copvoc.persist.impl.model.personal.PojoCardBatch;
import ru.dantalian.copvoc.persist.sqlite.model.DbCardBatch;
import ru.dantalian.copvoc.persist.sqlite.model.mappers.DbCardBatchMapper;

@Service
public class SqlitePersistBatchManager implements PersistBatchManager {

	@Autowired
	private JdbcTemplate db;

	@Autowired
	private DbCardBatchMapper mapper;

	@Autowired
	private SqlitePersistLanguageManager mLangManager;

	@Override
	public CardBatch createBatch(final String aUser, final String aName, final String aDescription,
			final Language aSource, final Language aTarget) throws PersistException {
		final UUID uuid = UUID.randomUUID();
		final String id = uuid.toString();
		final String source = LanguageUtils.asString(aSource);
		final String target = LanguageUtils.asString(aTarget);
		try {
			db.update("INSERT INTO card_batch (id, name, description, user, source, target) VALUES (?, ?, ? ,?, ?, ?)",
					id, aName, aDescription, aUser, source, target);
			return toCardBatch(new DbCardBatch(uuid, aName, aDescription, aUser, source, target));
		} catch (final DataAccessException e) {
			throw new PersistException("Failed create a CardBatch", e);
		}
	}

	@Override
	public void updateBatch(final String aUser, final CardBatch aCardBatch) throws PersistException {
		try {
			final String id = aCardBatch.getId().toString();
			final CardBatch batch = getBatch(aUser, aCardBatch.getId());
			if (batch == null) {
				throw new PersistException("CardBatch not found id: " + id);
			}
			db.update("UPDATE card_batch SET name = ?, description = ?"
					+ " WHERE \"user\" = ?"
					+ " AND id = ?",
					aCardBatch.getName(),
					aCardBatch.getDescription(),
					aUser,
					id);
		} catch (final DataAccessException e) {
			throw new PersistException("Failed create a CardBatch", e);
		}
	}

	@Override
	public CardBatch getBatch(final String aUser, final UUID aId) throws PersistException {
		try {
			final List<DbCardBatch> list = db.query("select * from card_batch where \"user\" = ? AND id = ?",
					new Object[] {
							aUser,
							aId.toString()
					},
					mapper);
			return toCardBatch(CollectionUtils.lastElement(list));
		} catch (final DataAccessException e) {
			throw new PersistException("Failed to get a CardBatch by id " + aId, e);
		}
	}

	@Override
	public CardBatch queryBatch(final String aUser, final String aName) throws PersistException {
		try {
			final List<DbCardBatch> list = db.query("select * from card_batch where \"user\" = ? AND name = ?",
					new Object[] {
							aUser,
							aName
					},
					mapper);
			return toCardBatch(CollectionUtils.lastElement(list));
		} catch (final DataAccessException e) {
			throw new PersistException("Failed to get a CardBatch by name " + aName, e);
		}
	}

	@Override
	public List<CardBatch> listBatches(final String aUser) throws PersistException {
		try {
			final List<DbCardBatch> list = db.query("select * from card_batch where \"user\" = ?",
					new Object[] {
							aUser
					},
					mapper);
			return list.stream()
					.map(this::toCardBatchSilent)
					.collect(Collectors.toList());
		} catch (final DataAccessException e) {
			throw new PersistException("Failed to get a CardBatch by user", e);
		}
	}

	private CardBatch toCardBatchSilent(final DbCardBatch aDbCardBatch) {
		try {
			return toCardBatch(aDbCardBatch);
		} catch (final PersistException e) {
			throw new RuntimeException("Failed to convert " + aDbCardBatch, e);
		}
	}

	private CardBatch toCardBatch(final DbCardBatch aDbCardBatch) throws PersistException {
		if (aDbCardBatch == null) {
			return null;
		}
		Language source = LanguageUtils.asLanguage(aDbCardBatch.getSource());
		Language target = LanguageUtils.asLanguage(aDbCardBatch.getTarget());
		source = mLangManager.getLanguage(source.getName(), source.getCountry(), source.getVariant());
		target = mLangManager.getLanguage(target.getName(), target.getCountry(), target.getVariant());
		return new PojoCardBatch(aDbCardBatch.getId(), aDbCardBatch.getName(), aDbCardBatch.getDescription(),
				aDbCardBatch.getUser(), source, target);
	}

}
