package com.usc.app.us.user;

import java.util.HashMap;
import java.util.Map;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.obj.api.InvokeContext;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.USCServerBeanProvider;

public class CreateUserWKEvnAction extends AbstractAction
{

	@Override
	public Object executeAction() throws Exception
	{
		if (disable())
		{
			return new ActionMessage(flagFalse, RetSignEnum.MODIFY, "当前用户已存在工作环境无需创建");
		}
		USCObject[] objects = context.getSelectObjs();
		for (USCObject user : objects)
		{
			String userID = user.getID();
			String userName = user.getFieldValueToString("SNAME");
			boolean userState = user.getFieldValue("OPSTATE") == null ? false
					: ((Boolean) user.getFieldValue("OPSTATE"));
			Map<String, Object> initMap = new HashMap<String, Object>();
			initMap.put("SUID", userID);
			initMap.put("SUNAME", userName);
			initMap.put("SUSTATE", userState);

			InvokeContext applicationContext = USCServerBeanProvider.getContext("WKCLIENT", context.getUserName());
			applicationContext.setFormData(initMap);
			USCObject uscObject = applicationContext.createObj("WKCLIENT");
			if (uscObject != null)
			{
				user.setFieldValue("WKCONTEXT", true);
				user.save(context);
			}
		}

		return new ActionMessage(flagTrue, RetSignEnum.MODIFY, "工作环境创建成功", objects);
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
