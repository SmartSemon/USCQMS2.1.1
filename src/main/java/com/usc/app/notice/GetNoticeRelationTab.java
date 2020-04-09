package com.usc.app.notice;

import java.util.HashMap;
import java.util.Map;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.activiti.ActCommonUtil;
import com.usc.obj.api.USCObject;
import com.usc.obj.util.USCObjectQueryHelper;
import com.usc.server.util.BeanFactoryConverter;
import com.usc.test.mate.jsonbean.USCObjectJSONBean;
import com.usc.test.mate.resource.ServiceToWbeClientResource;

/**
 * @Author: lwp
 * @DATE: 2019/12/19 14:45
 * @Description:
 **/
public class GetNoticeRelationTab extends AbstractAction {
	@Override
	public Object executeAction() throws Exception {
		String noticeId = (String) context.getExtendInfo("itemId");
		// 获取中间表关联对象的建模数据与关联数据
		String relSql = "FK_NOTICE_ID = '" + noticeId + "' AND del = 0 GROUP BY ITEMNO";
		USCObject[] noticeObjects = USCObjectQueryHelper.getObjectsByCondition("NOTICE_ITEMNO", relSql);
		if (noticeObjects != null)
		{
			Object[] relationTab = new Object[noticeObjects.length];
			for (int i = 0; i < noticeObjects.length; i++)
			{
				Map<String, Object> model = new HashMap<>();
				model.put("itemNo", noticeObjects[i].getFieldValueToString("ITEMNO"));
				model.put("facetype", 2);
				model.put("userName", context.getUserName());
				model.put("page", 1);
				model.put("pageId", 0);
				String params = ActCommonUtil.toJsonString(model);
				ServiceToWbeClientResource serviceToWbeClientResource = new ServiceToWbeClientResource();
				Map<String, Object> relationModel = serviceToWbeClientResource
						.getModelData(BeanFactoryConverter.getJsonBean(USCObjectJSONBean.class, params), null);
				relationTab[i] = relationModel;
			}
			return relationTab;
		}
		return null;

	}

	@Override
	public boolean disable() throws Exception {
		return false;
	}
}
