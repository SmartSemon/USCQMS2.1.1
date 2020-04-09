package com.usc.app.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.util.tran.InternationalFormat;

public class BatchAddObjAction extends AbstractAction {

	@Override
	public Object executeAction() throws Exception {
		String itemNo = (String) context.getExtendInfo("itemNo");
		String userName = (String) context.getExtendInfo("userName");
		List<Map<String, Object>> selectData = (List<Map<String, Object>>) context.getExtendInfo("hData");
		List<Map<String, Object>> mapFields = (List<Map<String, Object>>) context.getExtendInfo("otherParam");
		Object[] object = new Object[selectData.size()];
		for (int i = 0; i < selectData.size(); i++)
		{
			Map<String, Object> newData = new HashMap<>();
			for (Map<String, Object> mapField : mapFields)
			{
				newData.put((String) mapField.get("tfield"), selectData.get(i).get(mapField.get("sfield")));
				newData.put("ITEMNO", itemNo);
				newData.put("STATE", "C");
				newData.put("MUSER", userName);
				newData.put("MTIME", new Date());
				newData.put("DEL", 0);
			}
			context.setFormData(newData);
			object[i] = context.createObj(itemNo);
		}
		return new ActionMessage(true, RetSignEnum.NEW,
				InternationalFormat.getFormatMessage("BatchAdd_Success", context.getLocale()), object);
	}

	@Override
	public boolean disable() throws Exception {
		return true;
	}

}
