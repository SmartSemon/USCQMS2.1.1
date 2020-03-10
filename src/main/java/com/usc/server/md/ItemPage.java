package com.usc.server.md;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.usc.server.util.BeanConverter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemPage implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 4729151317011970475L;

	private String id;
	private String no;
	private String name;
	private double width;
	private Integer columns;
	private Integer defaultc;
	private Integer peptide;

	private List<ItemPeptidePage> peptidePageList;
	private Map<String, ItemPeptidePage> peptidePageMap;
	private List<ItemPageField> pageFieldList;
	private Map<String, ItemPageField> pageFieldMap;

	public ItemPageField getItemPageField(String no) {
		return this == null ? null : (this.pageFieldMap == null) ? null : this.pageFieldMap.get(no);
	}

	@Override
	public String toString() {

		return this == null ? null : this.no + "-" + this.name;

	}

	public Map<String, Object> toMap() {
		return BeanConverter.toMap(this);
	}
}
