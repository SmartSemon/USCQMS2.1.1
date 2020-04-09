package com.usc.app.action.demo.zc;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.ims.config.action.EndpointEnum;
import com.usc.app.util.SendMessageUtils;
import com.usc.obj.api.USCObject;
import com.usc.obj.util.USCObjectQueryHelper;

public class ConfirmReviewFileAction extends AbstractAction {

	@Override
	public Object executeAction() throws Exception {
		USCObject object = context.getSelectedObj();
		object.setFieldValue("ISCONSULT", true);
		if (object.save(context))
		{
			USCObject uscObject = USCObjectQueryHelper.getObjectByCondition("QCDOCUMENTMANAGEMENT",
					"DEL=0 and ID=(select ITEMAID from REL_QC_DOCUMENT_MANAGEMENT_OBJ where DEL=0 and ITEMA='QCDOCUMENTMANAGEMENT' and ITEMB='"
							+ context.getItemNo() + "' and ITEMBID='" + object.getID() + "')");
			SendMessageUtils.sendToUser(EndpointEnum.RefreshUnread, context, null, "notice", "质量系统文件已查阅提醒",
					"你下发的文件：" + context.getItemInfo().getName() + "我已查阅，文件编号：" + uscObject.getFieldValueToString("NO"),
					context.getUserName(), object.getFieldValueToString("CUSER"));
		}
		return ActionMessage.creator(true, RetSignEnum.MODIFY, "已确认并通知下发人", object);
	}

	@Override
	public boolean disable() throws Exception {
		USCObject object = context.getSelectedObj();
		return (object.getFieldValueToBoolen("ISCONSULT")
				|| !object.getFieldValueToString("LOWERRUNNER").equals(context.getUserName())) ? true : false;
	}

}
