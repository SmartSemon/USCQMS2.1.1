package com.usc.app.notice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.activiti.ActCommonUtil;
import com.usc.dto.Dto;
import com.usc.dto.impl.MapDto;
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
public class GetUnqualified extends AbstractAction {
	@Override
	public Object executeAction() throws Exception {
		String noticeId = (String) context.getExtendInfo("itemId");
		// 获取中间表关联对象的建模数据与关联数据
		String relSql = "SELECT * FROM NOTICE_ITEMNO WHERE FK_NOTICE_ID = '" + noticeId + "' AND del = 0";
//		Map<String,Object>
		// 获取建模数据
		Dto dto = new MapDto();
		dto.put("itemNo", "UNQUALIFIED");
		dto.put("facetype", 2);
		dto.put("itemGridNo", "default");
		dto.put("itemPropertyNo", "default");
		dto.put("itemRelationPageNo", "default");
		dto.put("userName", context.getUserName());
		dto.put("condition", "DEL = 0");
		dto.put("page", 1);
		dto.put("pageId", 1);
		String params = ActCommonUtil.toJsonString(dto);
		ServiceToWbeClientResource serviceToWbeClientResource = new ServiceToWbeClientResource();
		Map<String, Object> resultModel = serviceToWbeClientResource
				.getModelData(BeanFactoryConverter.getJsonBean(USCObjectJSONBean.class, params), null);
		Dto subList = new MapDto();
		subList.put("resultModel", resultModel);
		// 通过关联表获取不合格品汇报的关联数据Unqualified
		String unqualifiedSql = "del = 0 AND EXISTS(SELECT 1 FROM NOTICE_ITEMNO A WHERE A.DEL=0 AND A.ITEMNO_ID=UNQUALIFIED.ID  AND A.FK_NOTICE_ID='"
				+ noticeId + "') order by CTIME";
		USCObject[] objects = USCObjectQueryHelper.getObjectsByCondition("UNQUALIFIED", unqualifiedSql);
		if (objects != null)
		{
			List<Map<String, Object>> NoticeHasUnqualified = new ArrayList<Map<String, Object>>(objects.length);
			for (USCObject obj : objects)
			{ NoticeHasUnqualified.add(obj.getFieldValuesJSON(flagTrue)); }
			subList.put("resultItemList", NoticeHasUnqualified);
		}

		return subList;
	}

	@Override
	public boolean disable() throws Exception {
		return false;
	}
}
