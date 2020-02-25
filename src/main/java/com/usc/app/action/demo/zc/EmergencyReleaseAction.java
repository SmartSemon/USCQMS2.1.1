package com.usc.app.action.demo.zc;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.obj.api.USCObject;

/**
 * @author Semon
 *紧急放行
 */
public class EmergencyReleaseAction extends AbstractAction {

	@Override
	public Object executeAction() throws Exception {
		USCObject[] uscObjects = context.getSelectObjs();
		for (USCObject uscObject : uscObjects) {
			uscObject.setFieldValue("INSSTATE", "E");
			uscObject.save(context);
		}
		return new ActionMessage(true, RetSignEnum.MODIFY, StandardResultTranslate.translate("Action_Update_1"), uscObjects);
	}

	@Override
	public boolean disable() throws Exception {
		USCObject[] uscObjects = context.getSelectObjs();
		for (USCObject uscObject : uscObjects) {
			Object st = uscObject.getFieldValue("INSSTATE");
			if (st!=null && !st.toString().equals("A")) {
				return true;
			}
		}
		return false;
	}

}
