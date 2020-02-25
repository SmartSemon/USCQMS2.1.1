package com.usc.app.us.user;

import java.util.HashMap;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.obj.api.InvokeContext;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.USCServerBeanProvider;

public class RemoveUserWKEvnAction extends AbstractAction
{

	@Override
	public Object executeAction() throws Exception
	{

		if (disable())
		{
			return new ActionMessage(flagFalse, RetSignEnum.MODIFY, "当前用户已存在工作环境无需创建");
		}
		USCObject[] objects = context.getSelectObjs();
		for (USCObject object : objects)
		{
			String id = object.getID();
			String userName = object.getFieldValueToString("SNAME");
			HashMap<String, Object> initMap = new HashMap<String, Object>();
			initMap.put("SUID", id);
			initMap.put("SUNAME", userName);
			InvokeContext applicationContext = USCServerBeanProvider.getContext("wkclient", context.getUserName());
			applicationContext.setFormData(initMap);
			USCObject uscObject = applicationContext.createObj("wkclient");
			if (uscObject != null)
			{
				object.setFieldValue("WKCONTEXT", true);
				object.save(context);
			}
		}

		return new ActionMessage(flagTrue, RetSignEnum.MODIFY, "当前用户已存在工作环境无需创建", objects);
	}

	@Override
	public boolean disable() throws Exception
	{
		USCObject[] objects = context.getSelectObjs();
		for (USCObject object : objects)
		{
			boolean hasWkcontext = object.getFieldValueToBoolen("WKCONTEXT");
			if (hasWkcontext)
			{
				return true;
			}
		}

		return false;
	}

}
