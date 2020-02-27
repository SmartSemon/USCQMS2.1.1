package com.usc.app.action.demo.zc;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.obj.api.USCObject;

/**
 * @author Semon
 *接收预警消息
 */
public class EarlyWarningFollowUpAction extends AbstractAction {

	@Override
	public Object executeAction() throws Exception 
	{
		USCObject object = context.getSelectedObj();
		object.setFieldValue("DWSTATE", "B");
		object.save(context);
		return new ActionMessage(true, RetSignEnum.MODIFY, "已跟进预警内容", object);
	}

	@Override
	public boolean disable() throws Exception {
		return !"A".equals(context.getSelectedObj().getFieldValue("DWSTATE"));
	}

}
