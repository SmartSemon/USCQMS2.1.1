package com.usc.server.mq.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.usc.server.md.mapper.ItemRelBeanFactoryConverter;
import com.usc.server.mq.ItemMQMenu;

public class ItemMQMenuRowMapper implements RowMapper<ItemMQMenu> {
	public ItemMQMenuRowMapper()
	{
	}

	@Override
	public ItemMQMenu mapRow(ResultSet rs, int rowNum) throws SQLException {
		ItemMQMenu info = ItemRelBeanFactoryConverter.getBean(ItemMQMenu.class, rs);
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
