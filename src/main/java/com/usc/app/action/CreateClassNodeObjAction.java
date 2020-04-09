package com.usc.app.action;

import java.util.Map;

import com.usc.app.action.a.AbstractClassAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.util.tran.InternationalFormat;
import com.usc.obj.api.USCObject;

public class CreateClassNodeObjAction extends AbstractClassAction {

	@Override
	public Object executeAction() throws Exception {
		String nodeID = nodeObj.getID();
		String nodeItemNo = nodeObj.getFieldValueToString("ITEMNO");
		String itemNo = super.getClassNodeItemNo();
		Map<String, Object> data = ActionParamParser.getFieldVaues(context.getItemInfo(),
				super.getClassNodeItemProperty(), context.getFormData());
		data.put("PID", nodeID);
		data.put("ITEMNO", nodeItemNo);
		context.setFormData(data);
		USCObject object = context.createObj(itemNo);
		return new ActionMessage(true, RetSignEnum.NEW,
				InternationalFormat.getFormatMessage("Action_Create_1", context.getLocale()), object);
	}

}
