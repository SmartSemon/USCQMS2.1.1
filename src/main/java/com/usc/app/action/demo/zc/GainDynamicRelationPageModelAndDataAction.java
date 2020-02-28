package com.usc.app.action.demo.zc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.usc.app.action.a.AbstractRelationAction;
import com.usc.app.action.mate.MateFactory;
import com.usc.app.query.search.i.AbstractSearchAction;
import com.usc.obj.api.USCObject;
import com.usc.obj.util.USCObjectQueryHelper;
import com.usc.server.md.ItemInfo;
import com.usc.test.mate.resource.ModelUtils;

public class GainDynamicRelationPageModelAndDataAction extends AbstractRelationAction implements AbstractSearchAction {

	@Override
	public Object executeAction() throws Exception {

		Object itemno = root.getFieldValue("ITEMNO");
		String condition = root.getFieldValueToString("SQLCONDITION");
		if (itemno != null && condition != null)
		{
			HashMap<String, Object> ret = new HashMap<String, Object>(8);
			ret.put("flag", true);
			ret.put("info", "ok");
			ItemInfo itemInfo = MateFactory.getItemInfo(itemno.toString());
//			List<ItemMenu> itemMenus = itemInfo.getItemMenuList();
//			UserAuthority.authorityMenus(context.getUserInformation(), itemMenus);
//			ret.put("itemMenus", itemMenus);
			ModelUtils.getClientAllPageData(context.getUserInformation(), ret, itemInfo, "default", "default",
					"default", 2);
			USCObject[] objects = USCObjectQueryHelper.getObjectsByCondition(itemno.toString(), condition);
			List<Object> dataList = new ArrayList<Object>();
			if (objects != null)
			{
				for (USCObject object : objects)
				{ dataList.add(object.getFieldValuesJSON(true)); }
				ret.put("dataList", dataList);
				return ret;
			}
		}
		return failedOperation();
	}

}
