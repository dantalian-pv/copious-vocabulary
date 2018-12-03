package ru.dantalian.copvoc.persist.sqlite.model.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import ru.dantalian.copvoc.persist.sqlite.model.DbPrincipal;

@Service
public class DbPrincipalMapper implements RowMapper<DbPrincipal> {

	@Override
	public DbPrincipal mapRow(final ResultSet aRs, final int aRowNum) throws SQLException {
		return new DbPrincipal(aRs.getString("name"), aRs.getString("description"));
	}

}
