package com.usc.app.action;

import java.util.Map;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.util.tran.StandardResultTranslate;

public class CreateTreeObjAction extends AbstractAction {

	@Override
	public Object executeAction() throws Exception {
		if (!context.getItemInfo().containsField("PID"))
		{ return failedOperation(StandardResultTranslate.translate("Object_Miss_TreeField")); }
		Map<String, Object> newData = ActionParamParser.getFieldVaues(context.getItemInfo(), context.getItemPage(),
				context.getFormData());
		String pid = (String) context.getExtendInfo("pid");
		newData.put("PID", pid);
		context.setFormData(newData);
		CreateObjAction createObjAction = new CreateObjAction();
		createObjAction.setApplicationContext(context);
		return createObjAction.action();
	}

	@Override
	public boolean disable() throws Exception {
		return true;
	}

}
