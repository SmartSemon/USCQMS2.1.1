package com.usc.app.action.demo.zc;

import java.util.Map;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.ims.config.action.EndpointEnum;
import com.usc.app.util.SendMessageUtils;
import com.usc.obj.api.USCObject;
import com.usc.util.ObjectHelperUtils;

public class QualityDepartmentClosedAction extends AbstractAction
{

	@Override
	public Object executeAction() throws Exception
	{
		Map<String, Object> data = context.getFormData();
		Object qmes = data.get("OBJDESCRIPTION");
		Object objection = data.get("OBJECTION");
		if (ObjectHelperUtils.isEmpty(objection))
		{
			return new ActionMessage(flagFalse, null, "请选择有无异议");
		}
		boolean b = "N".equals(String.valueOf(objection).toUpperCase()) ? true : false;
		USCObject[] objects = context.getSelectObjs();

		for (USCObject uscObject : objects)
		{
			String title = "你负责处理的质量问题";
			String mes = "";
			uscObject.setFieldValue("OBJDESCRIPTION", qmes);
			uscObject.setFieldValue("OBJECTION", objection);
			if (b)
			{
				title += "已关闭";
				uscObject.setFieldValue("DWSTATE", "F");
			} else
			{
				title += "质量部有异议，请重新处理";
				uscObject.setFieldValue("DWSTATE", "D");
			}
			uscObject.save(context);
			mes = title + ".问题编号：" + uscObject.getFieldValueToString("NO") + "\n质量部门意见：" + (qmes == null ? "无" : qmes);
			SendMessageUtils.sendToUser(EndpointEnum.RefreshRead, context, new USCObject[]
			{ uscObject }, (b ? "news" : "todo"), title, mes, context.getUserName(),
					uscObject.getFieldValueToString("PJUSER"));

		}
		if (b)
		{
			return new ActionMessage(flagTrue, RetSignEnum.MODIFY, "质量问题已关闭", objects);
		}
		return new ActionMessage(flagTrue, RetSignEnum.MODIFY, "质量问题已退回责任部门相关责任人", objects);
	}

	@Override
	public boolean disable() throws Exception
	{
		USCObject[] objects = context.getSelectObjs();
		String user = context.getUserName();
		for (USCObject uscObject : objects)
		{
			String dwState = (String) uscObject.getFieldValue("DWSTATE");
			String qauser = uscObject.getFieldValueToString("QAUSER");
			if (!"E".equals(dwState) || !user.equals(qauser))
			{
				return true;
			}
		}
		return false;
	}

}
