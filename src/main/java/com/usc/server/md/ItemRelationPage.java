package com.usc.server.md;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemRelationPage implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 3518102973564367286L;
	private String id;
	private String no;
	private String name;
	private int defaultc;

	private Map<String, ItemRelationPageSign> itemRelationPageSignMap;
	private List<ItemRelationPageSign> itemRelationPageSignList;

	@Override
	public ItemRelationPage clone()
	{
		ItemRelationPage itemRelationPage = new ItemRelationPage();
		itemRelationPage.setId(id);
		itemRelationPage.setNo(no);
		itemRelationPage.setName(name);
		itemRelationPage.setDefaultc(defaultc);
		itemRelationPage.setItemRelationPageSignList(itemRelationPageSignList);
		itemRelationPage.setItemRelationPageSignMap(itemRelationPageSignMap);
		return itemRelationPage;
	}

	@Override
	public String toString()
	{
		return this.no + "-" + this.name;
	}

}
