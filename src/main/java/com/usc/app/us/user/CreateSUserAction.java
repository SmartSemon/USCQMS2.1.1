package com.usc.app.us.user;

import java.util.HashMap;
import java.util.Map;

import com.usc.app.action.ActionParamParser;
import com.usc.app.action.a.AbstractRelationAction;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.util.UserInfoUtils;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.autho.MD5.MD5Util;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.api.impl.USCServerBeanProvider;
import com.usc.server.init.InitUserInfo;

public class CreateSUserAction extends AbstractRelationAction
{

	@Override
	public Object executeAction() throws Exception
	{
		Map<String, Object> userData = ActionParamParser.getFieldVaues(context.getItemInfo(), context.getItemPage(),
				context.getFormData());
		userData.put("PASSWORD", MD5Util.getMD5Ciphertext(UserInfoUtils.getDefaultPassWord()));
		context.setFormData(userData);
		USCObject newObj = context.createObj(context.getItemNo());
		if (newObj != null)
		{
			Map<String, Object> relationData = getRelationData(getRoot(), newObj);

			ApplicationContext applicationContext = (ApplicationContext) USCServerBeanProvider
					.getContext(getRelationShip().getRelationItem(), context.getUserName());
			applicationContext.setFormData(relationData);
			applicationContext.createObj(getRelationShip().getRelationItem());
			InitUserInfo.addUser(newObj.getFieldValueToString("SNAME"));
			return new ActionMessage(flagTrue, RetSignEnum.NEW, StandardResultTranslate.translate("Action_Create_1"),
					newObj);
		}
		return failedOperation();
	}

	private Map<String, Object> getRelationData(USCObject root, USCObject newObj)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ITEMA", root.getItemNo());
		map.put("ITEMAID", root.getID());
		map.put("ITEMB", newObj.getItemNo());
		map.put("ITEMBID", newObj.getID());
		return map;
	}

}
