package com.usc.app.action.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.task.util.TaskActionResult;
import com.usc.app.ims.config.action.EndpointEnum;
import com.usc.app.util.SendMessageUtils;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.type.task.TaskObject;
import com.usc.server.util.SystemTime;

public class ReleaseTaskAction extends AbstractAction
{

	@Override
	public Object executeAction() throws Exception
	{
		USCObject[] objects = context.getSelectObjs();
		List<Map> maps = new ArrayList<Map>();
		for (USCObject taskObject : objects)
		{
			taskObject.setFieldValue("TSTATE", "B");
			taskObject.setFieldValue("DLUSER", context.getUserInformation().getUserName());
			taskObject.setFieldValue("DLTIME", SystemTime.getTimestamp());
			taskObject.save(context);
			SendMessageUtils.sendToUser(EndpointEnum.RefreshUnread, context, objects, "notice", "你有新的待办任务",
					"你有新的待办任务，任务为：" + taskObject.getFieldValueToString("NAME"), context.getUserName(),
					taskObject.getFieldValueToString("EXECUTOR"));
			maps.add(taskObject.getFieldValuesJSON(flagTrue));
		}

		return TaskActionResult.getResult(maps, "Delivered_Task_Successfully", "D");
	}

	@Override
	public boolean disable() throws Exception
	{
		USCObject[] objects = context.getSelectObjs();
		for (USCObject object : objects)
		{
			TaskObject task = (TaskObject) object;
			if (!task.getTaskState().equals("C"))
			{
				return true;
			}
		}
		return false;
	}

}
