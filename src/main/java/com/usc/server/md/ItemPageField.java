package com.usc.server.md;

import java.io.Serializable;
import java.util.Map;

import com.usc.server.util.BeanConverter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemPageField implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 4729151317011970475L;

	private String id;
	private String no;
	private String fieldName;

	private String FType;
	private Integer FLength;
	private Integer allowNull;
	private Integer accuracy;
	private Integer only;
	private String defaultV;
	private String remark;
	private Integer type;
	private String editor;
	private String editParams;
	private Integer editAble;
	private Integer wline;

	private Integer sort;

	private String caption;
	private String name;
	private String enName;

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
