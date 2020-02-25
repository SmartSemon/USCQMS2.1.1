package com.usc.app.action;

import java.util.Map;

import com.usc.app.action.a.AbstractRelationAction;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.api.impl.USCServerBeanProvider;

public class AddRelationShipObjAction extends AbstractRelationAction
{

	@Override
	public Object executeAction() throws Exception
	{
		USCObject[] objects = context.getSelectObjs();
		if (objects == null)
		{
			return successfulOperation();
		}
		for (USCObject uscObject : objects)
		{
			Map<String, Object> relMap = GetRelationData.getData(root, uscObject);
			ApplicationContext applicationContext = (ApplicationContext) USCServerBeanProvider
					.getContext(getRelationShip().getRelationItem(), context.getUserName());
			applicationContext.setFormData(relMap);
			applicationContext.createObj(applicationContext.getItemNo());
		}
		return new ActionMessage(flagTrue, RetSignEnum.ADD, StandardResultTranslate.translate("Action_AddRelation_1"));
	}

}
