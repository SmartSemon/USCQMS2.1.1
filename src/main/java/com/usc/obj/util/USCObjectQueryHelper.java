package com.usc.obj.util;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.usc.app.action.mate.MateFactory;
import com.usc.app.exception.ThrowException;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.USCServerBeanProvider;
import com.usc.server.jdbc.base.DataBaseUtils;
import com.usc.util.ObjectHelperUtils;

/**
 * @description USCObject查询工具
 * @author Semon
 * @return USCObject...
 */
public class USCObjectQueryHelper {
	/**
	 * @param itemNo 对象标识
	 * @param id     对象数据ID
	 * @return
	 */
	public static USCObject getObjectByID(@NotNull String itemNo, @NotNull String id) {
		if (ObjectHelperUtils.isEmpty(itemNo) || ObjectHelperUtils.isEmpty(id))
		{ return null; }
		try
		{
			Map<String, Object> data = USCServerBeanProvider.getItemBean().getRequestItemByID(itemNo, id);
			if (!ObjectHelperUtils.isEmpty(data))
			{ return NewUSCObjectHelper.newObject(itemNo, data); }
		} catch (Exception e)
		{
			ThrowException.throwException(e);
		}
		return null;

	}

	/**
	 * @param itemNo    对象标识
	 * @param condition 查询条件
	 * @return
	 */
	public static USCObject getObjectByCondition(@NotNull String itemNo, @NotNull String condition) {
		USCObject[] objects = getObjectsByCondition(itemNo, condition);
		return ObjectHelperUtils.isEmpty(objects) ? null : objects[0];
	}

	/**
	 * @param itemNo    对象标识
	 * @param condition 查询条件
	 * @return
	 */
	public static USCObject[] getObjectsByCondition(@NotNull String itemNo, @NotNull String condition) {
		if (ObjectHelperUtils.isEmpty(itemNo) || ObjectHelperUtils.isEmpty(condition))
		{ return null; }
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

	/**
	 * @param itemNo    对象标识
	 * @param condition 查询条件
	 * @param page      页码
	 * @return
	 */
	public static USCObject[] getObjectsByConditionLimit(@NotNull String itemNo, @NotNull String condition, int page) {
		if (ObjectHelperUtils.isEmpty(itemNo) || ObjectHelperUtils.isEmpty(condition))
		{ return null; }
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

	/**
	 * @param rel_item 关系表对象表名
	 * @param itemA    对象A标识
	 * @param itemB    对象B标识
	 * @param itemAID  对象A对象数据ID
	 * @param page     查询页码
	 * @return
	 */
	public static USCObject[] getRelationObjectsByConditionLimit(@NotNull String rel_item, @NotNull String itemA,
			@NotNull String itemB, @NotNull String itemAID, int page) {
		if (ObjectHelperUtils.isEmpty(rel_item) || ObjectHelperUtils.isEmpty(itemA) || ObjectHelperUtils.isEmpty(itemB)
				|| ObjectHelperUtils.isEmpty(itemAID))
		{ return null; }
		String condition = "DEL=0 and exists(select 1 from " + rel_item + " where DEL=0 and ITEMA='" + itemA + "' and "
				+ "ITEMB='" + itemB + "' and ITEMBID=" + itemB + ".ID and ITEMAID='" + itemAID + "')";
		try
		{
			USCObject[] objects = getObjectsByCondition(itemB, condition + DataBaseUtils.getLimit(page));
			return objects;
		} catch (Exception e)
		{
			ThrowException.throwException(e);
		}
		return null;

	}

	/**
	 * @param nodeID               节点数据ID
	 * @param itemNo               业务对象标识
	 * @param classTableName分类关系表名
	 * @return
	 */
	public static USCObject[] getClassObjects(@NotNull String nodeID, @NotNull String itemNo,
			@NotNull String classTableName) {
		String condition = "EXISTS(SELECT 1 FROM " + classTableName + " WHERE del=0  AND itemid="
				+ MateFactory.getItemInfo(itemNo).getTableName() + ".id AND nodeid='" + nodeID + "')";

		return getObjectsByCondition(itemNo, condition);
	}

	/**
	 * @param nodeID         节点数据ID
	 * @param itemNo         业务对象标识
	 * @param classTableName 分类关系表名
	 * @param page           查询页码
	 * @return
	 */
	public static USCObject[] getClassObjectsLimit(@NotNull String nodeID, @NotNull String itemNo,
			@NotNull String classTableName, int page) {
		String condition = "EXISTS(SELECT 1 FROM " + classTableName + " WHERE del=0  AND itemid="
				+ MateFactory.getItemInfo(itemNo).getTableName() + ".id AND nodeid='" + nodeID + "')";

		return getObjectsByConditionLimit(itemNo, condition, page);
	}
}
