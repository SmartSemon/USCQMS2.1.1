package com.usc.app.action.task;

import java.util.Map;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.task.util.TaskActionResult;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.type.task.TaskObject;
import com.usc.server.util.SystemTime;

/**
 * @Author: lwp
 * @DATE: 2019/10/29 15:28
 * @Description: 任务拒绝
 **/
public class TaskRejectionAction extends AbstractAction
{

	@Override
	public Object executeAction() throws Exception
	{
		USCObject[] objects = context.getSelectObjs();
		Map<String, Object> formData = context.getFormData();
		// 遍历需要驳回的任务
		for (USCObject uscObject : objects)
		{
			TaskObject object = (TaskObject) uscObject;
			object.setFieldValue("TSTATE", "C");
			object.setFieldValue("SUBDESCRIPTION", formData.get("SUBDESCRIPTION"));
			object.setFieldValue("RETIME", SystemTime.getTimestamp());
			object.save(context);
		}
		return TaskActionResult.getResult(null, "Rejection_Task_Successful", "D");
	}

	@Override
	public boolean disable() throws Exception
	{
		return true;
	}

}
