package ru.dantalian.copvoc.persist.sqlite.model.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.sqlite.model.DbCardBatch;

@Service
public class DbCardBatchMapper implements RowMapper<DbCardBatch> {

	@Override
	public DbCardBatch mapRow(final ResultSet aRs, final int aRowNum) throws SQLException {
		return new DbCardBatch(UUID.fromString(aRs.getString("id")),
				aRs.getString("name"),
				aRs.getString("description"),
				aRs.getString("user"),
				aRs.getString("source"),
				aRs.getString("target"));
	}

}
