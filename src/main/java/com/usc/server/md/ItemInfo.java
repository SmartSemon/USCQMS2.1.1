package com.usc.server.md;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.usc.util.ObjectHelperUtils;

import lombok.Data;

@Data
public class ItemInfo implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 5265877330470031945L;

	private String id;
	private String itemNo;
	private String tableName;
	private Integer isLife;
	private Integer type;
	private Integer sitem;
	private String queryFields;
	private String briefExp;
	private String implClass;
	private String colors;

	private String caption;
	private String name;
	private String enName;

	private Map<String, ItemField> itemFieldMap = null;
	private List<ItemField> itemFieldList = null;
	private Map<String, ItemMenu> itemMenuMap = null;
	private List<ItemMenu> itemMenuList = null;
	private Map<String, ItemPage> itemPageMap = null;
	private List<ItemPage> itemPageList = null;
	private Map<String, ItemGrid> itemGridMap = null;
	private List<ItemGrid> itemGridList = null;
	private Map<String, ItemRelationPage> relationPageMap = null;
	private List<ItemRelationPage> relationPageList = null;

	private ItemPage defaultItemPage = null;
	private ItemGrid defaultItemGrid = null;
	private ItemRelationPage defaultItemRelationPage = null;

	private List<ItemField> supQueryFieldList = null;

	public ItemInfo()
	{
	}

	public boolean isFileObj() {
		return this.type == 1 ? true : false;

	}

	@Override
	public ItemInfo clone() {
		ItemInfo info = new ItemInfo();
		info.setId(id);
		info.setItemNo(itemNo);
		info.setBriefExp(briefExp);
		info.setColors(colors);
		info.setDefaultItemGrid(defaultItemGrid);
		info.setDefaultItemPage(defaultItemPage);
		info.setDefaultItemRelationPage(defaultItemRelationPage);
		info.setEnName(enName);
		info.setImplClass(implClass);
		info.setIsLife(isLife);
		info.setItemFieldList(itemFieldList);
		info.setItemFieldMap(itemFieldMap);
		info.setItemGridList(itemGridList);
		info.setItemGridMap(itemGridMap);
		info.setItemMenuList(itemMenuList);
		info.setItemMenuMap(itemMenuMap);
		info.setItemPageList(itemPageList);
		info.setItemPageMap(itemPageMap);
		info.setName(name);
		info.setQueryFields(queryFields);
		info.setRelationPageList(relationPageList);
		info.setRelationPageMap(relationPageMap);
		info.setSitem(sitem);
		info.setTableName(tableName);
		info.setSupQueryFieldList(supQueryFieldList);
		info.setType(type);
		return info;
	}

	/**
	 * @return the fieldType
	 */
	public ItemField getItemField(String no) {
		return this.itemFieldMap == null ? null : itemFieldMap.get(no);
	}

	/**
	 * @return the menuType
	 */
	public ItemMenu getItemMenu(String implclass) {
		return this == null ? null : (this.itemMenuMap == null) ? null : this.itemMenuMap.get(implclass);
	}

	/**
	 * @return the itemPage
	 */
	public ItemPage getItemPage(String no) {
		return (no == null || String.valueOf(no).equals("null") || "".equals(no)) ? this.getDefaultItemPage()
				: (this.itemPageMap == null ? null : itemPageMap.get(no));
	}

	/**
	 * @return the itemGrid
	 */
	public ItemGrid getItemGrid(String no) {
		return (no == null || String.valueOf(no).equals("null") || "".equals(no)) ? this.getDefaultItemGrid()
				: (this.itemGridMap == null ? null : itemGridMap.get(no));
	}

	/**
	 * @return the relationPageMap
	 */
	public ItemRelationPage getItemRelationPage(String no) {
		return (no == null || String.valueOf(no).equals("null") || "".equals(no)) ? this.getDefaultItemRelationPage()
				: (this.relationPageMap == null ? null : relationPageMap.get(no));
	}

	/**
	 * @return the defaultItemPage
	 */
	public ItemPage getDefaultItemPage() {
		if (defaultItemPage != null)
		{ return defaultItemPage; }
		if (itemPageList == null)
		{ return null; }
		for (ItemPage p : itemPageList)
		{
			if (p.getDefaultc() == 1)
			{ return p; }
		}
		return itemPageList.get(0);
	}

	/**
	 * @param defaultItemPage the defaultItemPage to set
	 */
	public void setDefaultItemPage(ItemPage defaultItemPage) {
		this.defaultItemPage = defaultItemPage;
	}

	/**
	 * @return the defaultItemGrid
	 */
	public ItemGrid getDefaultItemGrid() {
		if (defaultItemGrid != null)
		{ return defaultItemGrid; }
		if (itemGridList == null)
		{ return null; }
		for (ItemGrid g : itemGridList)
		{
			if (g.getDefaultc() == 1)
			{ return g; }
		}
		return itemGridList.get(0);
	}

	public boolean containsField(String field) {

		return this.getItemField(field) != null;

	}

	/**
	 * @param defaultItemGrid the defaultItemGrid to set
	 */
	public void setDefaultItemGrid(ItemGrid defaultItemGrid) {
		this.defaultItemGrid = defaultItemGrid;
	}

	/**
	 * @return the defaultItemRelationPage
	 */
	public ItemRelationPage getDefaultItemRelationPage() {
		if (defaultItemRelationPage != null)
		{ return defaultItemRelationPage; }
		if (relationPageList == null)
		{ return null; }
		for (ItemRelationPage g : relationPageList)
		{
			if (g.getDefaultc() == 1)
			{ return g; }
		}
		return relationPageList.get(0);
	}

	/**
	 * @param defaultItemRelationPage the defaultItemRelationPage to set
	 */
	public void setDefaultItemRelationPage(ItemRelationPage defaultItemRelationPage) {
		this.defaultItemRelationPage = defaultItemRelationPage;
	}

	public List<ItemField> getQueryFieldList() {
		if (this.queryFields == null)
		{ return null; }
		if (ObjectHelperUtils.isEmpty(this.itemFieldMap))
		{ return null; }
		List<ItemField> fields = new ArrayList<ItemField>();
		String[] fieldStrings = this.queryFields.split(",");
		for (String fieldString : fieldStrings)
		{
			ItemField field = this.itemFieldMap.get(fieldString);
			if (field == null)
			{ continue; }
			if (!"DATETIME".equals((field.getFType())))
			{ fields.add(field); }

		}
		return fields;
	}

	@Override
	public String toString() {
		return this.itemNo + "-" + this.name;
	}

}
