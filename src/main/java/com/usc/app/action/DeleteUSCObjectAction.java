package com.usc.app.action;

import java.util.HashMap;
import java.util.Map;

import com.usc.app.action.a.DUSCObjAction;
import com.usc.app.util.tran.InternationalFormat;
import com.usc.obj.api.USCObject;

public class DeleteUSCObjectAction extends DUSCObjAction {

	@Override
	public Object executeAction() throws Exception {
		USCObject[] objects = context.getSelectObjs();
		for (USCObject uscObject : objects)
		{ uscObject.delete(context); }
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("flag", true);
		result.put("info", InternationalFormat.getFormatMessage("Action_Delete_1", context.getLocale()));
		return result;
	}

	@Override
	public boolean disable() throws Exception {
		USCObject[] objects = context.getSelectObjs();
		for (USCObject uscObject : objects)
		{
			if ("F".equals(uscObject.getFieldValue("STATE")))
			{ return true; }
		}
		return false;
	}

}
