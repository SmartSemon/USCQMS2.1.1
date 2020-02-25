package com.usc.app.action.task;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.type.task.TaskObject;
import com.usc.obj.api.type.task.TaskUtil;
import com.usc.server.util.SystemTime;

public class ReceivingTaskAction extends AbstractAction
{

	@Override
	public Object executeAction() throws Exception
	{
		USCObject[] objects = context.getSelectObjs();
		for (USCObject uscObject : objects)
		{
			TaskObject taskObject = (TaskObject) uscObject;
			taskObject.setFieldValue("TSTATE", "C");
			taskObject.setFieldValue("GTIME", SystemTime.getTimestamp());
			taskObject.save(context);

//            //lwp修改，发起待办通知
//            //获取任务执行认（也就是用户id）
//            String leaderID = uscObject.getID();
//            String taskName = uscObject.getFieldValueToString("NAME");
//
//            //生成一条待办消息
//            Map<String, Object> notice = new HashMap<>();
//            notice.put("TITLE", taskName);
//            notice.put("TYPE", "notice");
//            notice.put("STATUS", 0);
//            ApplicationContext NoticeApplicationContext = new ApplicationContext("NOTICE", context.getUserName());
//            NoticeApplicationContext.setFormData(notice);
//            NoticeApplicationContext.createObj("NOTICE");

		}

//		return TaskActionResult.getResult(null, "Received_Task_Successfully", "M");
		return new ActionMessage(flagTrue, RetSignEnum.MODIFY, "成功接受任务", objects);
	}

	/**
	 * 遍历输出对象是否都有输出数据
	 *
	 * @param outObj 输出对象
	 * @param taskId 任务id
	 * @return boolean
	 */
	private boolean hasOutputBusinessItems(USCObject outObj, String taskId)
	{
		// 判断每个输出对象是否有输出数据
		USCObject[] outs = TaskUtil.getOutputBusinessItems(outObj.getID(), taskId);
		if (outs == null)
		{
			return false;
		}
		return true;
	}

	@Override
	public boolean disable() throws Exception
	{
		USCObject[] objects = context.getSelectObjs();
		if (objects == null)
		{
			return false;
		}
		for (USCObject uscObject : objects)
		{
			TaskObject taskObject = (TaskObject) uscObject;
			if (!taskObject.getTaskState().equals("B"))
			{
				return true;
			}
			if (!taskObject.getExecutor().equals(context.getUserInformation().getUserName()))
			{
				return true;
			}
		}
		return false;
	}

}
