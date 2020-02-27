package com.usc.app.action.demo.zc;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.obj.api.USCObject;

public class DisableVendorProductsAction extends AbstractAction {

	@Override
	public Object executeAction() throws Exception 
	{
		USCObject[] selectObjs = context.getSelectObjs();
		for(USCObject object:selectObjs) 
		{
			object.setFieldValue("STOPUSING", true);
			object.save(context);
		}
		return new ActionMessage(true, RetSignEnum.MODIFY, "选中供应商产品已停用", selectObjs);
	}

	@Override
	public boolean disable() throws Exception {
		for(USCObject object:context.getSelectObjs())
		{
			if (object.getFieldValueToBoolen("STOPUSING")) 
			{
				return true;
			}
		}
		return false;
	}

}
