package com.usc.server.md.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.usc.server.md.ItemPeptidePage;

public class PeptidePageRowMapper implements RowMapper<ItemPeptidePage> {
	public PeptidePageRowMapper()
	{
	}

	@Override
	public ItemPeptidePage mapRow(ResultSet rs, int rowNum) throws SQLException {
		ItemPeptidePage info = ItemRelBeanFactoryConverter.getBean(ItemPeptidePage.class, rs);
		return info;
	}

	protected Map<String, Object> createColumnMap(int columnCount) {
		return new LinkedCaseInsensitiveMap<>(columnCount);
	}

	protected String getColumnKey(String columnName) {
		return columnName;
	}

	@Nullable
	protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
		return JdbcUtils.getResultSetValue(rs, index);
	}

}
