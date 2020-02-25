package com.usc.test.mate.jsonbean;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.alibaba.fastjson.JSON;
import com.usc.app.action.mate.MateFactory;
import com.usc.server.md.ModelRelationShip;

import lombok.Data;

@Data
public class USCRelationObjectJSONBean extends USCObjectUpdateJSONBean
{
	@NotNull
	public String ITEMA;
	public String RELATIONSHIPNO;
	public List<Map> RDATA;

	public USCRelationObjectJSONBean()
	{
		super();
	}

	public Map getItemAObj()
	{
		return JSON.parseObject(ITEMA, Map.class);
	}

	public ModelRelationShip getModelRelationShip()
	{
		return this.RELATIONSHIPNO == null ? null : MateFactory.getRelationShip(RELATIONSHIPNO);
	}

	@Override
	public String toString()
	{
		return this.RELATIONSHIPNO;

	}
}
