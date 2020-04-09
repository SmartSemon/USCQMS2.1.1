package com.usc.server.md;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ModelQueryView implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -2073770527905585241L;
	private String id;
	private String no;

	private String itemNo;
	private String wcondition;

	private String caption;
	private String name;
	private String enName;

	private List<ItemMenu> itemMenus;
	private ItemInfo itemInfo;
	private Integer isLife;
	private ItemGrid itemGrid;
	private ItemPage itemPropertyPage;

	@Override
	public String toString() {
		return this == null ? null : this.no + "-" + this.name;

	}

}
