package com.usc.app.action;

import java.util.HashMap;
import java.util.Map;

import com.usc.obj.api.USCObject;

public class GetRelationData
{
	public static Map<String, Object> getData(USCObject root, USCObject newObj)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ITEMA", root.getItemNo());
		map.put("ITEMAID", root.getID());
		map.put("ITEMB", newObj.getItemNo());
		map.put("ITEMBID", newObj.getID());
		return map;
	}
}
