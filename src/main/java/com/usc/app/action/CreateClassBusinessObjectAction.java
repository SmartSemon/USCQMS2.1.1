package com.usc.app.action;

import java.util.HashMap;
import java.util.Map;

import com.usc.app.action.a.AbstractClassAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.util.tran.InternationalFormat;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.api.impl.USCServerBeanProvider;

public class CreateClassBusinessObjectAction extends AbstractClassAction {

	@Override
	public Object executeAction() throws Exception {
		Map<String, Object> map = ActionParamParser.getFieldVaues(context.getItemInfo(), context.getItemPage(),
				context.getFormData());
		context.setFormData(map);
		USCObject object = context.createObj(context.getItemNo());

		if (object != null)
		{
			String classItemNo = super.getClassItemInfo().getItemNo();
			Map<String, Object> clrData = new HashMap<String, Object>();
			clrData.put("NODEID", nodeObj.getID());
			clrData.put("ITEMID", object.getID());
			ApplicationContext applicationContext = (ApplicationContext) USCServerBeanProvider
					.getContext(getClassItemInfo().getItemNo(), context.getUserName());
			applicationContext.setItemNo(classItemNo);
			applicationContext.setFormData(clrData);
			applicationContext.createObj(applicationContext.getItemNo());
			return new ActionMessage(true, RetSignEnum.NEW,
					InternationalFormat.getFormatMessage("Action_Create_1", context.getLocale()), object);
		} else
		{
			return context.getExtendInfo("CreateResult");
		}

	}

}
