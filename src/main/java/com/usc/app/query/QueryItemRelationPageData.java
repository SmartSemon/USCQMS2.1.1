package com.usc.app.query;

import com.usc.app.action.a.AbstractRelationAction;
import com.usc.app.action.mate.MateFactory;
import com.usc.app.action.utils.ActionMessage;
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
		String rel = MateFactory.getItemInfo(getRelationShip().getRelationItem()).getTableName();
		String condition = "DEL=0 and exists(select 1 from " + rel + " where DEL=0 and ITEMA='" + itemA + "' and "
				+ "ITEMB='" + itemB + "' and ITEMBID=" + itemB + ".ID and ITEMAID='" + itemAID + "')";
		USCObject[] objects = USCObjectQueryHelper.getObjectsByConditionLimit(itemB, condition, getDataPage(context));
//		List dataList = DBUtil.getRelationItemResult(itemA, MateFactory.getItemInfo(itemB), rel, itemAID, 1);
		return new ActionMessage(flagTrue, RetSignEnum.QUERY, "", objects);
	}

}
