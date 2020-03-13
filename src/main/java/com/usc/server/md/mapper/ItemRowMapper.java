package com.usc.server.md.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.usc.server.md.ItemField;
import com.usc.server.md.ItemInfo;

public class ItemRowMapper implements RowMapper<ItemInfo> {
	public ItemRowMapper()
	{
	}

	@Override
	public ItemInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		ItemInfo info = ItemBeanFactoryConverter.getBean(ItemInfo.class, rs);
		List<ItemField> supQueryFields = new ArrayList<ItemField>();
		List<ItemField> fields = info.getItemFieldList();
		if (fields != null)
		{
			for (ItemField itemField : fields)
			{
				int sup = itemField.getSupQuery();
				if (sup == 1)
				{
					itemField.setEditAble(1);
					supQueryFields.add(itemField);
				}
			}
			info.setSupQueryFieldList(supQueryFields);
		} else
		{
			System.out.println(info.getItemNo() + ":" + info.getName());
		}

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
