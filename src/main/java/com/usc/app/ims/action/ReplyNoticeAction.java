package com.usc.app.ims.action;

import java.util.Map;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.ims.config.action.EndpointEnum;
import com.usc.app.util.SendMessageUtils;
import com.usc.app.wxdd.WxDdMessageService;
import com.usc.obj.api.USCObject;
import com.usc.util.SpringContextUtil;

public class ReplyNoticeAction extends AbstractAction
{

	@Override
	public Object executeAction() throws Exception
	{
		Map<String, Object> mess = context.getFormData();
		String title = (String) mess.get("TITLE");
		String message = (String) mess.get("SMESSAGE");
		String sendUserName = context.getUserName();
		String toUserName = (String) mess.get("USERNAME");

		WxDdMessageService wxddService = SpringContextUtil.getBean(WxDdMessageService.class);
		wxddService.sendTextMessage(title, toUserName, null, null);
		SendMessageUtils.sendToUser(EndpointEnum.RefreshUnread, context, null, "notice", title, message, sendUserName,
				toUserName.split(","));
		return new ActionMessage(flagTrue, RetSignEnum.NOT_DEAL_WITH, "消息发送成功");
	}

	@Override
	public boolean disable() throws Exception
	{
		USCObject[] objects = context.getSelectObjs();
		for (USCObject uscObject : objects)
		{
			String userName = uscObject.getFieldValueToString("USERNAME");
			if (!context.getUserName().equals(userName))
			{
				return true;
			}
		}

		return false;
	}

}
