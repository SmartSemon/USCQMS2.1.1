package com.usc.app.action.demo.zc;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.obj.api.USCObject;

public class CloseComplaintAction extends AbstractAction {

	@Override
	public Object executeAction() throws Exception {
		USCObject[] objects = context.getSelectObjs();
		for (USCObject uscObject : objects) {
			uscObject.setFieldValue("DWSTATE", "C");
			uscObject.save(context);
		}
		return new ActionMessage(true, RetSignEnum.MODIFY, "问题已关闭", objects);
	}

	@Override
	public boolean disable() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
