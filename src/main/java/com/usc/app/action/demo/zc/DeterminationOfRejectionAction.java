package com.usc.app.action.demo.zc;

import java.util.Map;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.ims.config.action.EndpointEnum;
import com.usc.app.util.SendMessageUtils;
import com.usc.obj.api.USCObject;

/**
 * @Author: lwp
 * @DATE: 2019/12/20 17:22
 * @Description: 拒绝责任判定
 **/
public class DeterminationOfRejectionAction extends AbstractAction
{
	@Override
	public Object executeAction() throws Exception
	{
		Map<String, Object> taskData = context.getFormData();
		String subdescription = (String) taskData.get("SUBDESCRIPTION");
		String qauser = (String) taskData.get("QAUSER");
		USCObject[] objects = context.getSelectObjs();
		for (USCObject u : objects)
		{
			u.setFieldValue("DWSTATE", "C");
			u.setFieldValue("SUBDESCRIPTION", subdescription);
			u.setFieldValue("QAUSER", qauser);
			u.save(context);
		}
		SendMessageUtils.sendToUser(EndpointEnum.RefreshRead, context, objects, "todo", "请重新判定责任单位和责任人",
				"请重新判定责任单位和责任人", context.getUserName(), qauser);
		return new ActionMessage(flagTrue, RetSignEnum.MODIFY, "已通知责任人", objects);
	}

	@Override
	public boolean disable() throws Exception
	{
		String userName = context.getUserName();
		USCObject[] objects = context.getSelectObjs();
		for (USCObject uscObject : objects)
		{
			String dwState = (String) uscObject.getFieldValue("DWSTATE");
			String pjuser = uscObject.getFieldValueToString("PJUSER");
			if (!"B".equals(dwState) || !userName.equals(pjuser))
			{
				return true;
			}
		}
		return false;
	}
}
