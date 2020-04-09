package com.usc.app.action;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.usc.app.action.a.AbstractRelationAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.obj.api.USCObject;

public class RemoveRelationObjAction extends AbstractRelationAction
{

	@Override
	public Object executeAction() throws Exception
	{
//		Map<String, Object> newData = ActionParamParser.getFieldVaues(context.getItemInfo(), context.getItemPage(),
//				context.getInitData());
//		context.setInitData(newData);
		USCObject newObj = null;
//		if (newObj != null)
//		{
//			Map relationData = getRelationData(getRoot(), newObj);
//			ApplicationContext applicationContext = (ApplicationContext) context.cloneContext();
//			applicationContext.setInitData(relationData);
//			applicationContext.createObj(getRelationShip().getRelationItem());
//		}
		return new ActionMessage(flagTrue, RetSignEnum.DELETE, StandardResultTranslate.translate("Action_Remove_1"),
				newObj);
	}

	private Map<String, Object> getRelationData(USCObject root, USCObject newObj)
	{
		Map<String, Object> map = new ConcurrentHashMap<String, Object>();
		map.put("itema", root.getItemNo());
		map.put("itemaid", root.getID());
		map.put("itemb", newObj.getItemNo());
		map.put("itembid", newObj.getID());
		return map;
	}

}
