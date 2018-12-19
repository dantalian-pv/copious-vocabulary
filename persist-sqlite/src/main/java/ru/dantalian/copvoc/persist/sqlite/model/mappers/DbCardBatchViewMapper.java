package ru.dantalian.copvoc.persist.sqlite.model.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.sqlite.model.DbCardBatchView;

@Service
public class DbCardBatchViewMapper implements RowMapper<DbCardBatchView> {

	@Override
	public DbCardBatchView mapRow(final ResultSet aRs, final int aRowNum) throws SQLException {
		return new DbCardBatchView(UUID.fromString(aRs.getString("id")),
				UUID.fromString(aRs.getString("batch_id")),
				aRs.getString("css"),
				aRs.getString("front"),
				aRs.getString("back"));
	}

}
