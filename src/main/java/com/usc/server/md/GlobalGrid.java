package com.usc.server.md;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.usc.server.util.BeanConverter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GlobalGrid implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = -9148923512210049627L;
	private String id;
	private String no;
	private String name;
	private String align;
	private int type;

	private List<ItemGridField> gridFieldList;
	private Map<String, ItemGridField> gridFieldMap;

	public ItemGridField getItemGridField(String no)
	{
		return this == null ? null : (this.gridFieldMap == null) ? null : this.gridFieldMap.get(no);
	}

	@Override
	public String toString()
	{
		return this == null ? null : this.no + "-" + this.name;
	}

	public Map<String, Object> toMap()
	{
		GlobalGrid gridType = this;
		return BeanConverter.toMap(gridType);

	}

}
