package com.usc.server.md;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.usc.cache.redis.RedisUtil;
import com.usc.server.jdbc.DBUtil;
import com.usc.util.ObjectHelperUtils;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class USCModelMate
{
	static RedisUtil redisUtil = RedisUtil.getInstanceOfObject();

	public static boolean containsObj(String no)
	{
		if (getItemInfo(no) != null)
			return true;
		if (getItemInfoByTable(no) != null)
			return true;
		return false;
	}

	public static ItemInfo getItemInfo(String itemNo)
	{
		if (itemNo != null)
		{
			Object info = redisUtil.hget("MODEL_ITEMDATA", itemNo.toUpperCase());
			if (info != null)
			{
				return (ItemInfo) info;
			}
		}

		return null;
	}

	public static ItemInfo getItemInfoByTable(String tableName)
	{
		if (tableName != null)
		{
			Object info = redisUtil.hget("MODEL_ITEMDATABYTABLE", tableName.toUpperCase());
			if (info != null)
			{
				return (ItemInfo) info;
			}
		}
		return null;
	}

	public static ModelRelationShip getRelationShipInfo(String relationShipNo)
	{
		if (relationShipNo != null)
		{
			Object info = redisUtil.hget("MODEL_RELATIONSHIPDATA", relationShipNo.toUpperCase());
			if (info != null)
			{
				return (ModelRelationShip) info;
			}
		}
		return null;
	}

	public static ModelQueryView getModelQueryViewInfo(String modelQueryViewNo)
	{
		if (modelQueryViewNo != null)
		{
			Object info = redisUtil.hget("MODEL_QUERYVIEWDATA", modelQueryViewNo.toUpperCase());
			if (info != null)
			{
				return (ModelQueryView) info;
			}
		}
		return null;
	}

	public static ModelClassView getModelClassViewInfo(String classViewNo)
	{
		if (classViewNo != null)
		{
			Object info = redisUtil.hget("MODEL_CLASSVIEWDATA", classViewNo.toUpperCase());
			if (info != null)
			{
				return (ModelClassView) info;

			}
		}

		return null;
	}

	public static boolean VerifyItemUniqueness(ItemInfo itemInfo, Map<String, Object> map) throws Exception
	{
		if (itemInfo == null || map == null)
		{
			return false;
		}

		return ObjectHelperUtils.isEmpty(DBUtil.getSQLResultByOnlyFieldObject(itemInfo, map)) ? false : true;

	}

	public static List<ItemField> getOnlyItemFields(ItemInfo itemInfo)
	{
		if (itemInfo == null)
		{
			return null;
		}
		List<ItemField> onlyFields = new ArrayList<ItemField>();
		for (ItemField itemField : itemInfo.getItemFieldList())
		{
			if (itemField.getType() == 0)
			{
				continue;
			}
			if (itemField.getOnly() == 1)
			{
				onlyFields.add(itemField);
			}
		}
		return ObjectHelperUtils.isEmpty(onlyFields) ? null : onlyFields;

	}

	public static ItemField getItemField(String objType, String field)
	{
		ItemInfo info = getItemInfo(objType.toUpperCase());
		if (info != null && info.containsField(field))
		{
			return info.getItemField(field);
		}
		return null;

	}

	public boolean containsField(Object itemInfo, String field)
	{
		ItemInfo info = null;
		if (itemInfo instanceof String)
		{
			info = getItemInfo(((String) itemInfo).toUpperCase());
		} else if (itemInfo instanceof ItemInfo)
		{
			info = (ItemInfo) itemInfo;
		}
		if (info == null)
		{
			return false;
		}
		return info.containsField(field);
	}

	public static GlobalGrid getGlobalGrid(String gridNo)
	{
		if (gridNo != null)
		{
			Object grid = redisUtil.hget("MODELGLOBALGRID", gridNo);
			if (grid != null)
			{
				return (GlobalGrid) grid;
			}
		}
		return null;
	}

}
