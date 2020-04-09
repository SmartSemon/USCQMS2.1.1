package com.usc.app.query;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.query.search.i.AbstractSearchAction;
import com.usc.obj.api.USCObject;
import com.usc.obj.util.USCObjectQueryHelper;
import com.usc.server.md.ItemInfo;

public class QuerySingleItemData extends AbstractAction implements AbstractSearchAction
{

	@Override
	public Object executeAction() throws Exception
	{
		String condition = getCondition(context);
		ItemInfo itemInfo = context.getItemInfo();
		USCObject[] objects = USCObjectQueryHelper.getObjectsByConditionLimit(itemInfo.getItemNo(), condition,
				getDataPage(context));
		return new ActionMessage(flagTrue, RetSignEnum.QUERY, "", objects);
	}

	@Override
	public boolean disable() throws Exception
	{
		return false;
	}

}
