package ru.dantalian.copvoc.persist.sqlite.managers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import ru.dantalian.copvoc.persist.api.PersistBatchViewManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.model.CardBatchView;
import ru.dantalian.copvoc.persist.impl.model.PojoCardBatchView;
import ru.dantalian.copvoc.persist.sqlite.model.DbCardBatchView;
import ru.dantalian.copvoc.persist.sqlite.model.mappers.DbCardBatchViewMapper;

@Service
public class SqlitePersistBatchViewManager implements PersistBatchViewManager {

	@Autowired
	private JdbcTemplate db;

	@Autowired
	private DbCardBatchViewMapper mapper;

	@Override
	public CardBatchView createBatchView(final UUID aBatchId, final String aCss, final String aFrontTpl, final String aBackTpl)
			throws PersistException {
		final UUID uuid = UUID.randomUUID();
		final String id = uuid.toString();
		try {
			db.update("INSERT INTO card_batch_view (id, batch_id, css, front, back) VALUES (?, ?, ? ,?, ?)",
					id, aBatchId.toString(), aCss, aFrontTpl, aBackTpl);
			return toCardBatchView(new DbCardBatchView(uuid, aBatchId, aCss, aFrontTpl, aBackTpl));
		} catch (final DataAccessException e) {
			throw new PersistException("Failed create a CardBatchView", e);
		}
	}

	@Override
	public void updateBatchView(final UUID aId, final String aCss, final String aFrontTpl, final String aBackTpl)
			throws PersistException {
		try {
			final String id = aId.toString();
			final CardBatchView batchView = getBatchView(aId);
			if (batchView == null) {
				throw new PersistException("CardBatch not found id: " + id);
			}
			db.update("UPDATE card_batch_view SET css = ?, front = ?, back = ?"
					+ " WHERE id = ?",
					aCss,
					aFrontTpl,
					aBackTpl,
					id);
		} catch (final DataAccessException e) {
			throw new PersistException("Failed to update a CardBatchView", e);
		}
	}

	@Override
	public CardBatchView getBatchView(final UUID aId) throws PersistException {
		try {
			final List<DbCardBatchView> list = db.query("select * from card_batch_view where id = ?",
					new Object[] {
							aId.toString()
					},
					mapper);
			return toCardBatchView(CollectionUtils.lastElement(list));
		} catch (final DataAccessException e) {
			throw new PersistException("Failed to get a CardBatchView by id " + aId, e);
		}
	}

	@Override
	public CardBatchView getBatchViewByBatchId(final UUID aBatchId) throws PersistException {
		try {
			final List<DbCardBatchView> list = db.query("select * from card_batch_view where batch_id = ?",
					new Object[] {
							aBatchId.toString()
					},
					mapper);
			return toCardBatchView(CollectionUtils.lastElement(list));
		} catch (final DataAccessException e) {
			throw new PersistException("Failed to get a CardBatchView by batchId " + aBatchId, e);
		}
	}

	private CardBatchView toCardBatchView(final DbCardBatchView aDbCardBatchView) {
		if (aDbCardBatchView == null) {
			return null;
		}
		return new PojoCardBatchView(aDbCardBatchView.getId(), aDbCardBatchView.getBatchId(),
				aDbCardBatchView.getCss(), aDbCardBatchView.getFrontTpl(), aDbCardBatchView.getBackTpl());
	}

}
