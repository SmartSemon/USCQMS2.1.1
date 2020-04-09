package com.usc.app.action.demo.zc;

import java.util.Map;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.obj.api.USCObject;
import com.usc.util.ObjectHelperUtils;

/**
 * @author SEMON
 *         <p>
 *         批量 通知相关责任人
 *         </p>
 */
public class NotifyResponsiblePersonAction extends AbstractAction
{

	@Override
	public Object executeAction() throws Exception
	{
		Map<String, Object> map = context.getFormData();
		if (ObjectHelperUtils.isEmpty(map))
		{
			return new ActionMessage(flagFalse, RetSignEnum.MODIFY, "请判定责任单位和责任人");
		}
		USCObject[] objects = context.getSelectObjs();
		for (USCObject uscObject : objects)
		{

			if (flagFalse)
			{

			}
		}
		return null;
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
		if (objects.length < 3)
		{
			return true;
		}
		int n = 1;
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
				if ((pjuser != null && pjuser1 != null) && pjuser.equals(pjuser1))
				{
					if (n > 2)
					{
						return true;
					}
				}
			}
			n++;
		}
		return false;
	}

}
