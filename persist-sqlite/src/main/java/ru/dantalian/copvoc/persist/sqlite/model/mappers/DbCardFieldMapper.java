package ru.dantalian.copvoc.persist.sqlite.model.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.api.model.CardFiledType;
import ru.dantalian.copvoc.persist.sqlite.model.DbCardField;

@Service
public class DbCardFieldMapper implements RowMapper<DbCardField> {

	@Override
	public DbCardField mapRow(final ResultSet aRs, final int aRowNum) throws SQLException {
		return new DbCardField(UUID.fromString(aRs.getString("batch_id")),
				aRs.getString("name"),
				CardFiledType.valueOf(aRs.getString("type")));
	}

}
