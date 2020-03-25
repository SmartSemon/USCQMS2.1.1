package com.usc.server.md;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemGridField implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -9148923512210049627L;
	private String id;
	private String no;
	private String fieldName;
	private String name;
	private String FType;
	private int FLength;
	private int allowNull;
	private int accuracy;
	private int only;
	private String defaultV;
	private String remark;
	private int type;
	private String editor;
	private String editParams;
	private String editAble;
	private Double width;
	private String align;
	private int screen;

	private int supLink;
	private String linkParams;

	private int sort;

	@Override
	public String toString() {
		return this == null ? this.getClass().getSimpleName() : this.no + "-" + this.name;
	}
}
