package com.usc.app.query.search.i;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.usc.app.query.rt.QueryReturnRequest;
import com.usc.app.util.SearchUtils;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.util.USCObjExpHelper;
import com.usc.server.md.ItemInfo;
import com.usc.util.ObjectHelperUtils;

public interface AbstractSearchAction extends QueryReturnRequest
{
	public final String CONDITION = "condition";
	public final String QUERYWORD = "queryWord";
	public final String DATAPAGE = "page";
	public final String SORTFIELDS = "sortFields";

	default String getCondition(ApplicationContext context)
	{
		Object condition = context.getExtendInfo(CONDITION);
		String cond = "";
		if (ObjectHelperUtils.isEmpty(condition))
		{
			cond = "del=0";
		} else
		{
			cond = USCObjExpHelper.parseUserNameValueInExpression(context, condition.toString());
		}
		if (cond.toLowerCase().contains(" order "))
		{

		}
		return cond + getSortFields(context);

	}

	default String getSortFields(ApplicationContext context)
	{
		String queryWord = (String) context.getExtendInfo(SORTFIELDS);
		if (queryWord == null)
		{
			queryWord = " ORDER BY CTIME";
		} else
		{
			ItemInfo itemInfo = context.getItemInfo();
			StringBuffer buffer = new StringBuffer(" ORDER BY ");
			JSONArray array = JSONArray.parseArray(queryWord);
			int size = array.size();
			for (int i = 0; i < size; i++)
			{
				JSONObject jsonObject = JSONObject.parseObject(array.getString(i));
				String field = itemInfo.getItemField(jsonObject.getString("field")).getFieldName();
				String sort = jsonObject.getString("sort");
				if (i > 0)
				{
					buffer.append(",");
				}
				buffer.append(field).append(" ").append(sort);
			}
			queryWord = buffer.toString();
		}

		return queryWord;

	}

	default String getQueryWord(ApplicationContext context)
	{
		String queryWord = (String) context.getExtendInfo(QUERYWORD);
		return queryWord;

	}

	default int getDataPage(ApplicationContext context)
	{
		Object object = context.getExtendInfo(DATAPAGE);
		int page = (Integer) (object == null ? 1 : object);
		return page;

	}

	default boolean hasQueryFields(ItemInfo itemInfo)
	{
		return SearchUtils.hasQueryFields(itemInfo);
	}
}
