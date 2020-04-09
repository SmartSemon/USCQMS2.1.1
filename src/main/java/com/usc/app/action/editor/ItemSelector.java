package com.usc.app.action.editor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.util.UserInfoUtils;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.obj.api.bean.UserInformation;
import com.usc.obj.api.impl.USCServerBeanProvider;
import com.usc.server.md.ItemGridField;
import com.usc.server.md.ItemInfo;
import com.usc.test.mate.resource.ModelUtils;

/**
 * @Author: lwp
 * @DATE: 2019/8/19 16:23
 * @Description:
 **/
public class ItemSelector extends AbstractAction {
	@Override
	public Object executeAction() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String itemNo = (String) context.getExtendInfo("itemNo");
//		List<ItemGridField> gridFieldList = MateFactory.getItemInfo(itemNo).getDefaultItemGrid().getGridFieldList();
		UserInformation userInformation = UserInfoUtils.getUserInformation(context.getUserName());
		ItemInfo itemInfo = ModelUtils.getItemInfo(itemNo, userInformation);
		List<ItemGridField> gridFieldList = ModelUtils.getItemGrid(itemInfo, null, userInformation).getGridFieldList();
		map.put("gridFieldList", gridFieldList);
		map.put("itemName", itemInfo.getName());
		String condition = (String) context.getExtendInfo("condition");
		String dataListSql = condition != null && !"".equals(condition) ? "DEL = 0 AND " + condition : "DEL = 0";
		map.put("dataList", USCServerBeanProvider.getItemBean().getObjectsByCondition(itemNo, dataListSql));
		return StandardResultTranslate.getResult("Action_Query", map);
	}

	@Override
	public boolean disable() throws Exception {
		return true;
	}
}
