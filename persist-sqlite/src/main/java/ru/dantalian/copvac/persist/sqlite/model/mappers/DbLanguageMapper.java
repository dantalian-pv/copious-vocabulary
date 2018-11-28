package ru.dantalian.copvac.persist.sqlite.model.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ru.dantalian.copvac.persist.sqlite.model.DbLanguage;

public class DbLanguageMapper implements RowMapper<DbLanguage> {

	@Override
	public DbLanguage mapRow(final ResultSet aRs, final int aRowNum) throws SQLException {
		return new DbLanguage(aRs.getString("name"),
				aRs.getString("country"),
				aRs.getString("variant"),
				aRs.getString("text"));
	}

}
