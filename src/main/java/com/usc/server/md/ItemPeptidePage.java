package com.usc.server.md;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.usc.server.util.BeanConverter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemPeptidePage implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 4729151317011970475L;

	private String id;
	private String no;
	private String name;
	private String rootid;

	private Integer sort;

	private List<ItemPageField> pageFieldList;
	private Map<String, ItemPageField> pageFieldMap;

	/**
	 * @return this.toMap()
	 */
	public Map<String, Object> toMap() {

		return BeanConverter.toMap(this);

	}

	/**
	 * @return this.toString()
	 */
	@Override
	public String toString() {
		return this.no + "-" + this.name;

	}
}
