package com.usc.obj.util;

import java.util.List;
import java.util.Map;

import com.usc.app.action.mate.MateFactory;
import com.usc.app.exception.ThrowException;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.USCServerBeanProvider;
import com.usc.server.jdbc.base.DataBaseUtils;
import com.usc.util.ObjectHelperUtils;

public class USCObjectQueryHelper
{
	public static USCObject getObjectByID(String itemNo, String id)
	{
		if (ObjectHelperUtils.isEmpty(itemNo) || ObjectHelperUtils.isEmpty(id))
		{
			return null;
		}
		try
		{
			Map<String, Object> data = USCServerBeanProvider.getItemBean().getRequestItemByID(itemNo, id);
			if (!ObjectHelperUtils.isEmpty(data))
			{
				return NewUSCObjectHelper.newObject(itemNo, data);
			}
		} catch (Exception e)
		{
			ThrowException.throwException(e);
		}
		return null;

	}

	public static USCObject getObjectByCondition(String itemNo, String condition)
	{
		USCObject[] objects = getObjectsByCondition(itemNo, condition);
		return ObjectHelperUtils.isEmpty(objects) ? null : objects[0];
	}

	public static USCObject[] getObjectsByCondition(String itemNo, String condition)
	{
		if (ObjectHelperUtils.isEmpty(itemNo) || ObjectHelperUtils.isEmpty(condition))
		{
			return null;
		}
		try
		{
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> datas = USCServerBeanProvider.getItemBean().getObjectsByCondition(itemNo,
					condition);
			if (!ObjectHelperUtils.isEmpty(datas))
			{
//				List<USCObject> list = new ArrayList<USCObject>(datas.size());
//				datas.forEach(data -> {
//					USCObject object = NewUSCObjectHelper.newObject(itemNo, data);
//					list.add(object);
//				});
//				return list.toArray(new USCObject[list.size()]);

				USCObject[] objects = new USCObject[datas.size()];
				for (int i = 0; i < datas.size(); i++)
				{
					USCObject object = NewUSCObjectHelper.newObject(itemNo, datas.get(i));
					objects[i] = object;
				}
				return objects;
			}
		} catch (Exception e)
		{
			ThrowException.throwException(e);
		}
		return null;

	}

	public static USCObject[] getObjectsByConditionLimit(String itemNo, String condition, int page)
	{
		if (ObjectHelperUtils.isEmpty(itemNo) || ObjectHelperUtils.isEmpty(condition))
		{
			return null;
		}
		try
		{
			USCObject[] objects = getObjectsByCondition(itemNo, condition + DataBaseUtils.getLimit(page));
			return objects;
		} catch (Exception e)
		{
			ThrowException.throwException(e);
		}
		return null;

	}

	public static USCObject[] getClassObjects(String nodeID, String itemNo, String classTableName)
	{
		String condition = "EXISTS(SELECT 1 FROM " + classTableName + " WHERE del=0  AND itemid="
				+ MateFactory.getItemInfo(itemNo).getTableName() + ".id AND nodeid='" + nodeID + "')";

		return getObjectsByCondition(itemNo, condition);
	}

	public static USCObject[] getClassObjectsLimit(String nodeID, String itemNo, String classTableName, int page)
	{
		String condition = "EXISTS(SELECT 1 FROM " + classTableName + " WHERE del=0  AND itemid="
				+ MateFactory.getItemInfo(itemNo).getTableName() + ".id AND nodeid='" + nodeID + "')";

		return getObjectsByConditionLimit(itemNo, condition, page);
	}
}
