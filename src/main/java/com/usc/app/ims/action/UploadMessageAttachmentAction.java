package com.usc.app.ims.action;

import java.util.Map;

import com.usc.app.action.CreateObjAction;
import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.obj.api.USCObject;

public class UploadMessageAttachmentAction extends AbstractAction {

	@Override
	public Object executeAction() throws Exception {
		USCObject object = context.getSelectedObj();
		String noticeID = object.getID();
		Map<String, Object> formdata = context.getFormData();
		formdata.put("NOTICEID", noticeID);

		context.setItemNo("NOTICE_ATTACHMENT");
		context.setFormData(formdata);
		CreateObjAction action = new CreateObjAction();
		action.setApplicationContext(context);

		ActionMessage actionMessage = (ActionMessage) action.action();
		actionMessage.setInfo("上传附件成功");
		actionMessage.setSign("E");
		return actionMessage;
	}

	@Override
	public boolean disable() throws Exception {
		String userName = context.getUserName();
		USCObject[] objects = context.getSelectObjs();
		for (USCObject uscObject : objects)
		{
			Boolean status = uscObject.getFieldValueToBoolen("STATUS");
			String sender = uscObject.getFieldValueToString("SENDERID");
			if (status || !userName.equals(sender))
			{ return true; }
		}
		return false;
	}

}
