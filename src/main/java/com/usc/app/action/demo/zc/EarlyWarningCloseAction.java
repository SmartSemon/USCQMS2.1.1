package com.usc.app.action.demo.zc;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.obj.api.USCObject;

/**
 * @author Semon
 *关闭预警消息
 */
public class EarlyWarningCloseAction extends AbstractAction {

	@Override
	public Object executeAction() throws Exception 
	{
		USCObject object = context.getSelectedObj();
		object.setFieldValue("DWSTATE", "C");
		object.save(context);
		return new ActionMessage(true, RetSignEnum.MODIFY, "预警内容已处理完成", object);
	}

	@Override
	public boolean disable() throws Exception 
	{
		return !"B".equals(context.getSelectedObj().getFieldValue("DWSTATE"));
	}

}
