package com.usc.app.query;

import com.usc.app.action.a.AbstractRelationAction;
import com.usc.app.action.mate.MateFactory;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.query.search.i.AbstractSearchAction;
import com.usc.obj.api.USCObject;
import com.usc.obj.util.USCObjectQueryHelper;

public class QueryItemRelationPageData extends AbstractRelationAction implements AbstractSearchAction {

	@Override
	public Object executeAction() throws Exception {
		String itemA = root.getItemNo();
		String itemAID = root.getID();
		String itemB = context.getItemNo();
		String relItem = MateFactory.getItemInfo(getRelationShip().getRelationItem()).getTableName();
		USCObject[] objects = USCObjectQueryHelper.getRelationObjectsByConditionLimit(relItem, itemA, itemB, itemAID,
				getDataPage(context));
		return ActionMessage.creator(flagTrue, RetSignEnum.QUERY, "", objects);
	}

}
