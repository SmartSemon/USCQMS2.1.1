package com.usc.app.query;

import com.usc.app.action.a.AbstractRelationAction;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.query.search.i.AbstractSearchAction;
import com.usc.obj.api.USCObject;
import com.usc.obj.util.USCObjExpHelper;
import com.usc.obj.util.USCObjectQueryHelper;

public class QueryItemRelationQueryViewData extends AbstractRelationAction implements AbstractSearchAction
{

	@Override
	public Object executeAction() throws Exception
	{
		String itemB = context.getItemNo();
		String queryViewCondition = getRelationQueryView().getWcondition();
		String condition = USCObjExpHelper.parseObjValueInExpression(root, queryViewCondition);
		USCObject[] objects = USCObjectQueryHelper.getObjectsByConditionLimit(itemB, condition, getDataPage(context));
		return new ActionMessage(flagTrue, RetSignEnum.QUERY, "", objects);
	}
}
