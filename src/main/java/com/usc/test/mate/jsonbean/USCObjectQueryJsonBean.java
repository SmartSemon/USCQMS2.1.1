package com.usc.test.mate.jsonbean;

import com.usc.app.action.mate.MateFactory;
import com.usc.server.md.ItemInfo;

import lombok.Data;

@Data
public class USCObjectQueryJsonBean extends USCObjectJSONBean {

	private String queryWord;

	public String condition;

	public Integer page;

	private String classNodeItemNo;
	private String classNodeItemPropertyNo;
	private String classItemNo;

	public USCObjectQueryJsonBean()
	{
		super();
	}

	@Override
	public ItemInfo getItemInfo() {
		return MateFactory.getItemInfo(itemNo);
	}

}
