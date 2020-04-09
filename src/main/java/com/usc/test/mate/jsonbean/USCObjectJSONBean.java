package com.usc.test.mate.jsonbean;

import java.util.Map;

import javax.validation.constraints.NotNull;

import com.alibaba.fastjson.JSONObject;
import com.usc.app.action.mate.MateFactory;
import com.usc.server.md.ItemInfo;

import lombok.Data;

@Data
public class USCObjectJSONBean {
	@NotNull
	public String userName;
	@NotNull
	public String itemNo;

	@NotNull
	public String itemGridNo;
	public String itemPropertyNo;
	public String itemRelationPageNo;

	public String pageId;

	public String viewNo;

	public Integer faceType;

	public Map<String, Object> data;

	public USCObjectJSONBean()
	{
		super();
	}

	public USCObjectJSONBean(String jsonString)
	{
		setFieldsValues(jsonString);
	}

	private void setFieldsValues(String jsonString) {

	}

	public USCObjectJSONBean(JSONObject jsonObject)
	{
		setFieldsValues(jsonObject);
	}

	private void setFieldsValues(JSONObject jsonObject) {

	}

	public ItemInfo getItemInfo() {
		return this.itemNo == null ? null : MateFactory.getItemInfo(itemNo);
	}

}
