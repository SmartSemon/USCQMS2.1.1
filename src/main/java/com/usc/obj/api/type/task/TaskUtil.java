package com.usc.obj.api.type.task;

import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.util.USCObjectQueryHelper;

public class TaskUtil
{
	public static USCObject[] getTaskInOrOutputData(ApplicationContext context, USCObject root, int page)
	{
//        return DBUtil.getSQLResultByConditionLimit(context.getItemInfo(), "del=0 AND taskid=?", new Object[]
//                {root.getID()}, new int[]
//                {Types.VARCHAR}, page);
		String itemNo = context.getItemNo();
		String condition = "del=0 AND taskid='" + root.getID() + "'";
		;
		return USCObjectQueryHelper.getObjectsByConditionLimit(itemNo, condition, page);
	}

	public static TaskObject getTaskObj(String itemNo, String id)
	{
		return (TaskObject) USCObjectQueryHelper.getObjectByID(itemNo, id);
	}

	public static boolean checkLeader(ApplicationContext context, USCObject root)
	{
		String cuser = root.getFieldValueToString("cuser");
		String leader = root.getFieldValueToString("LEADER");
		String user = context.getUserInformation().getUserName();
		if (user.equals(cuser) || user.equals(leader))
		{
			return true;
		}
		return false;
	}

	public static boolean checkExecutor(ApplicationContext context, USCObject root)
	{
		String executor = root.getFieldValueToString("EXECUTOR");
		String user = context.getUserInformation().getUserName();
		if (user.equals(executor))
		{
			return true;
		}
		return false;
	}

	public static boolean checkTaskState(ApplicationContext context, USCObject root)
	{
		Object tstate = root.getFieldValue("TSTATE");
		return "A".equals(tstate) || "E".equals(tstate);
	}

	public static USCObject[] getInputs(String taskID)
	{
		return USCObjectQueryHelper.getObjectsByCondition("TASKINPUT", "DEL=0 AND TASKID='" + taskID + "'");
	}

	public static USCObject[] getInputBusinessItems(String taskID)
	{
		return USCObjectQueryHelper.getObjectsByCondition("TASKINPUT", "DEL=0 AND PID='0' AND TASKID='" + taskID + "'");
	}

	/**
	 * 获取输出对象
	 * 
	 * @param taskID 任务ID
	 * @return 返回对象数组
	 * @Author: lwp
	 */
	public static USCObject[] getOutputs(String taskID)
	{
		return USCObjectQueryHelper.getObjectsByCondition("TASKOUTPUT",
				"DEL=0 AND PID='0' AND TASKID='" + taskID + "'");
	}

	/**
	 * 获取输出对象数据
	 * 
	 * @param taskID 任务ID
	 * @return 返回对象数组
	 * @Author: lwp
	 */
	public static USCObject[] getOutputBusinessItems(String pid, String taskID)
	{
		return USCObjectQueryHelper.getObjectsByCondition("TASKOUTPUT",
				"DEL=0 AND PID='" + pid + "'" + " AND TASKID='" + taskID + "'");
	}
}
