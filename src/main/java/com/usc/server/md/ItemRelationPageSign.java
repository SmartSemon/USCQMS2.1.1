package com.usc.server.md;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemRelationPageSign implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 3518102973564367286L;
	private String id;
	private String no;

	private String itemNo;
	private String rType;

	private String itemGrid;
	private String relevanceNo;
	private String relevanceName;
	private String param;
	private String icon;
	private Integer supQuery;
	private Object relationPageSign;

	private String caption;
	private String name;
	private String enName;

	private ItemPage itemRelationPropertyPage;
	private ModelRelationShip modelRelationShip;
	private ModelQueryView modelQueryView;
	private ModelClassView modelClassView;

	@Override
	public String toString() {
		return this.no + "-" + this.name;
	}
}
