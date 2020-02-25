package com.usc.test.mate.jsonbean;

import com.usc.app.action.mate.MateFactory;
import com.usc.server.md.ItemInfo;

import lombok.Data;

@Data
public class USCObjectQueryJsonBean extends USCObjectJSONBean
{

	private String QUERYWORD;

	public String CONDITION;

	public Integer PAGE;

	private String classNodeItemNo;
	private String classNodeItemPropertyNo;
	private String classItemNo;

	public USCObjectQueryJsonBean()
	{
		super();
	}

	public ItemInfo getItemInfo()
	{
		return MateFactory.getItemInfo(ITEMNO);
	}

}
