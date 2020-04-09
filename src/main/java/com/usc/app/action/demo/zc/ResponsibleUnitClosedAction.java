package com.usc.app.action.demo.zc;

import java.util.Map;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.ims.config.action.EndpointEnum;
import com.usc.app.util.SendMessageUtils;
import com.usc.obj.api.USCObject;

public class ResponsibleUnitClosedAction extends AbstractAction
{

	@Override
	public Object executeAction() throws Exception
	{
		Map<String, Object> formData = context.getFormData();
		Object endSign = formData.get("QAUSER");
		USCObject[] objects = null;
		if (endSign != null)
		{
			objects = context.getSelectObjs();
			for (USCObject uscObject : objects)
			{
				uscObject.setFieldValue("QAUSER", endSign);
				uscObject.setFieldValue("DWSTATE", "E");
				uscObject.save(context);

				SendMessageUtils.sendToUser(EndpointEnum.RefreshRead, context, new USCObject[]
				{ uscObject }, "todo", "有新的质量问题待你关闭", "有新的质量问题待你关闭。问题编号：" + uscObject.getFieldValueToString("NO"),
						context.getUserName(), uscObject.getFieldValueToString("QAUSER"));
			}
		} else
		{
			return new ActionMessage(flagTrue, RetSignEnum.NOT_DEAL_WITH, "请选择质保部门责任人！");
		}

		return new ActionMessage(flagTrue, RetSignEnum.MODIFY, "责任单位关闭完成", objects);
	}

	@Override
	public boolean disable() throws Exception
	{
		USCObject[] objects = context.getSelectObjs();
		String user = context.getUserName();
		for (USCObject uscObject : objects)
		{
			String dwState = (String) uscObject.getFieldValue("DWSTATE");
			String pjuser = uscObject.getFieldValueToString("PJUSER");
			if (!"D".equals(dwState) || !user.equals(pjuser))
			{
				return true;
			}
		}
		return false;
	}

}
