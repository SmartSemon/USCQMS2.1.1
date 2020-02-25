package com.usc.app.action.task;

import java.util.Map;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.task.util.TaskActionResult;
import com.usc.app.ims.config.action.EndpointEnum;
import com.usc.app.util.SendMessageUtils;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.type.task.TaskObject;
import com.usc.obj.util.USCObjectQueryHelper;
import com.usc.server.util.SystemTime;

/**
 * @Author: lwp
 * @DATE: 2019/10/29 15:17
 * @Description: 任务确认
 **/
public class TaskValidationAction extends AbstractAction
{
	@Override
	public Object executeAction() throws Exception
	{
		USCObject[] objects = context.getSelectObjs();
		Map<String, Object> formData = context.getFormData();
		Object endSign = formData.get("ENDSIGN");
		boolean sign = endSign == null ? false : ((boolean) endSign);
		// 遍历需要确认的任务
		for (USCObject uscObject : objects)
		{
			TaskObject object = (TaskObject) uscObject;

			object.setFieldValue("ENDDESCRIPTION", formData.get("ENDDESCRIPTION"));
			object.setFieldValue("ENDSIGN", endSign == null ? false : endSign);
			object.setFieldValue("CFTIME", SystemTime.getTimestamp());
			String title = "你提交的的任务确认";
			String msign = "news";
			if (sign)
			{
				title += "通过，任务完成";
				object.setFieldValue("TSTATE", "F");
				String taskNo = object.getNo();
				USCObject objectUF = USCObjectQueryHelper.getObjectByCondition("UNQUALIFIED",
						"del=0 AND no='" + taskNo + "'");
				if (objectUF != null)
				{
//					objectUF.setFieldValue("DWSTATE", "E");
//					objectUF.save(USCServerBeanProvider.getContext(context.getUserName(), objectUF));
					SendMessageUtils.sendToUser(EndpointEnum.RefreshRead, context, new USCObject[]
					{ objectUF }, "todo", "问题处理结束，任务已完成，请执行责任单位关闭操作", "问题编号：" + objectUF.getFieldValueToString("NO"),
							context.getUserName(), objectUF.getFieldValueToString("PJUSER"));
				}
			} else
			{
				msign = "notice";
				title += "未通过，任务被拒绝";
				object.setFieldValue("TSTATE", "E");
			}
			SendMessageUtils.sendToUser(EndpointEnum.RefreshRead, context, objects, msign, title,
					"任务编号：" + object.getFieldValueToString("NO") + "，任务名称为：" + object.getFieldValueToString("NAME"),
					context.getUserName(), object.getExecutor());
			object.save(context);
		}
		return TaskActionResult.getResult(null, "Validation_Task_Successfully", "D");
	}

	@Override
	public boolean disable() throws Exception
	{
		USCObject[] objects = context.getSelectObjs();
		for (USCObject uscObject : objects)
		{
			TaskObject object = (TaskObject) uscObject;
			if (!"D".equals(object.getTaskState()) || !context.getUserName().equals(object.getLeader()))
			{
				return true;
			}
		}
		return false;
	}
}
