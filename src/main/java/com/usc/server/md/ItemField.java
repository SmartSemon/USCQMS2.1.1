package com.usc.server.md;

import java.io.Serializable;
import java.util.Map;

import com.usc.server.md.field.FieldEditor;
import com.usc.server.util.BeanConverter;

import lombok.Data;

@Data
public class ItemField implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -2302809118871658860L;
	private String id;
	private String no;
	private String fieldName;

	private String caption;
	private String name;
	private String enName;

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
	private int supQuery;
	private int editAble;
	private int supLink;
	private String linkParams;

	private FieldEditor fieldEditor;

	public ItemField()
	{
	}

	public FieldEditor getFieldEditor() {
		if (this.fieldEditor == null)
		{
			setFieldEditor(new FieldEditor(this));
		} else
		{
			if (this.fieldEditor.getEditor() == null)
			{ this.fieldEditor.setEditor(editor); }
			if (this.fieldEditor.getEditParams() == null)
			{ this.fieldEditor.setEditParams(editParams); }
		}
		return fieldEditor;
	}

	public Map<String, Object> toMap() {
		ItemField fieldType = this;
		return BeanConverter.toMap(fieldType);
	}

	@Override
	public String toString() {
		return (this != null) ? (no + ":" + name) : null;

	}

}
