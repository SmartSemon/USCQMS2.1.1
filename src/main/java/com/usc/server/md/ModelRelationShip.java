package com.usc.server.md;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.usc.app.action.mate.MateFactory;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ModelRelationShip implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1723137020779401634L;

	private String id;
	private String no;

	private String relationItem;
	private String itemA;
	private String itemB;
	private String pItem;
	private String param;
	private String ship;
	private Integer supQuery;

	private String caption;
	private String name;
	private String enName;

	private List<ItemMenu> relationMenuList = null;
	private Map<String, ItemMenu> relationMenuMap = null;

	private List<ItemMenu> itemMenus;
	private ItemInfo itemInfo;
	private Integer isLife;
	private ItemGrid itemGrid;
	private ItemPage itemPropertyPage;
	private List<ItemRelationPageSign> itemRelationPage;

	public String getrelationTableName() {
		if (relationItem != null)
		{ return MateFactory.getItemInfo(this.relationItem).getTableName(); }
		return null;
	}

	@Override
	public String toString() {
		return this == null ? null : this.no + "-" + this.name;

	}

}
