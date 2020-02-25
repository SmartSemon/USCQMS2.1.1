package com.usc.app.action.demo.zc;

import java.util.Date;
import java.util.Map;

import com.usc.app.action.GetRelationData;
import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.ims.config.action.EndpointEnum;
import com.usc.app.util.SendMessageUtils;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.bean.UserInformation;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.api.impl.USCServerBeanProvider;
import com.usc.util.ObjectHelperUtils;

/**
 * @author SEMON
 *         <p>
 *         批量通知同一责任人
 *         </p>
 */
public class NotifySameResponsiblePersonAction extends AbstractAction
{

	@Override
	public Object executeAction() throws Exception
	{
		Map<String, Object> map = context.getFormData();
		if (ObjectHelperUtils.isEmpty(map))
		{
			return new ActionMessage(flagFalse, RetSignEnum.MODIFY, "请判定责任单位和责任人");
		}

		if (ObjectHelperUtils.isEmpty(map.get("NAME")))
		{
			return new ActionMessage(flagFalse, RetSignEnum.MODIFY, "请判定责任人");
		}
		String toUser = (String) map.get("NAME");
		ApplicationContext applicationContext = (ApplicationContext) USCServerBeanProvider.getContext("DRUNIT",
				context.getUserName());
		applicationContext.setFormData(map);
		USCObject[] objects = context.getSelectObjs();
		for (USCObject uscObject : objects)
		{
			uscObject.setFieldValue("DWSTATE", "B");
			uscObject.setFieldValue("PJUSER", toUser);
//			uscObject.setObjectFieldValues(map);
			createDRUNIT(applicationContext, uscObject);
			uscObject.save(context);
			SendMessageUtils.sendToUser(EndpointEnum.RefreshRead, context, objects, "todo", "请确认质量问题责任",
					"请确认质量问题责任。问题编号:" + uscObject.getFieldValueToString("NO"), context.getUserName(), toUser);
		}

		return new ActionMessage(flagTrue, RetSignEnum.MODIFY, "已通知责任人", objects);
	}

	/**
	 * <p>
	 * 创建判定责任单位
	 * </p>
	 *
	 * @param applicationContext 用户信息
	 * @param uscObject          问题对象
	 * @throws Exception
	 */
	private void createDRUNIT(ApplicationContext applicationContext, USCObject uscObject) throws Exception
	{
		UserInformation userInformation = applicationContext.getUserInformation();
		Map<String, Object> data = applicationContext.getFormData();
//		data.put("NO", uscObject.getFieldValueToString("PJUNIT"));
//		data.put("NAME", uscObject.getFieldValueToString("PJUSER"));
		data.put("DCSDEPARTMENT", userInformation.getDepartMentName());
		data.put("DESUSER", userInformation.getUserName());
		data.put("DECTIME", new Date());
		applicationContext.setFormData(data);
		USCObject object = applicationContext.createObj("DRUNIT");

		// 建立中间表数据
		ApplicationContext relatContext = (ApplicationContext) USCServerBeanProvider.getContext("REL_DRUNIT_OBJ",
				applicationContext.getUserName());
		relatContext.setFormData(GetRelationData.getData(uscObject, object));
		relatContext.createObj("REL_DRUNIT_OBJ");
	}

	@Override
	public boolean disable() throws Exception
	{
		// 如果不合格汇报数据处理状态为：已通知责任单位 B、责任单位处理中D、责任单位已关闭额E、已关闭F时
		// 不能再执行此操作
		USCObject object = context.getSelectedObj();
		String pjunit = object.getFieldValueToString("PJUNIT");
		String pjuser = object.getFieldValueToString("PJUSER");
		USCObject[] objects = context.getSelectObjs();
		for (USCObject uscObject : objects)
		{
			String dwState = (String) uscObject.getFieldValue("DWSTATE");
			if ("B".equals(dwState) || "D".equals(dwState) || "E".equals(dwState) || "F".equals(dwState))
			{
				return true;
			} else
			{
				String pjunit1 = uscObject.getFieldValueToString("PJUNIT");
				String pjuser1 = uscObject.getFieldValueToString("PJUSER");
				if (pjuser == null && pjuser1 != null)
				{
					pjuser = pjuser1;
				}
				if ((pjuser != null && pjuser1 != null) && !pjuser.equals(pjuser1))
				{
					return true;
				}
			}
		}
		return false;
	}

}
