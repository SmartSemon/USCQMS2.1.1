package com.usc.app.action.task;

import java.util.ArrayList;
import java.util.List;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.cache.PromptTranslationTools;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.action.task.util.TaskActionResult;
import com.usc.app.ims.config.action.EndpointEnum;
import com.usc.app.util.SendMessageUtils;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.type.task.TaskObject;
import com.usc.obj.api.type.task.TaskUtil;
import com.usc.obj.util.USCObjectQueryHelper;
import com.usc.server.util.SystemTime;

/**
 * @Author: lwp
 * @DATE: 2019/10/29 09:28
 * @Description: 任务提交
 **/

public class SubmitTaskAction extends AbstractAction
{

	@Override
	public Object executeAction() throws Exception
	{
		USCObject[] objects = context.getSelectObjs();
		List nameList = new ArrayList();
		for (USCObject uscObject : objects)
		{
			boolean uf = false;
			TaskObject object = (TaskObject) uscObject;
			// 获取输出对象遍历
			USCObject[] outObj = TaskUtil.getOutputs(object.getID());
			String taskNo = uscObject.getFieldValueToString("NO");

			// 先判断任务是否有输出对象，有则必须有输出数据
			if (outObj != null)
			{
				USCObject[] jzyf = queryUnqualified(taskNo);
				if (jzyf != null)
				{
					USCObject object2 = outObj[0];
					String itemNo = object2.getFieldValueToString("ITEMNO");
					if ("CORRECTION_PREVENTION".equals(itemNo))
					{
						object.addOutPutBusinessObjects(object2, jzyf);
						uf = true;
					}
				}
				boolean isAllOutputHasOutputBusinessItems = hasOutputBusinessItems(outObj, object.getID());
				if (!isAllOutputHasOutputBusinessItems)
				{
					// 有输出对象，但是有输出对象无输出数据，不修改数据，提示;
					nameList.add(uscObject.getFieldValue("NAME"));
					return new ActionMessage(flagFalse, null,
							PromptTranslationTools.translationMessage("NoOutput_Submit_Task", "task"));
				} else
				{
					// 有输出对象，输出对象都是有输出对象数据，修改数据
					object.setFieldValue("TSTATE", "D");
					object.setFieldValue("SBTIME", SystemTime.getTimestamp());
					object.save(context);
//					if (uf)
//					{
//						USCObject objectUF = USCObjectQueryHelper.getObjectByCondition("UNQUALIFIED",
//								"del=0 AND no='" + taskNo + "'");
//						if (objectUF != null)
//						{
//							objectUF.setFieldValue("DWSTATE", "E");
//							objectUF.save(USCServerBeanProvider.getContext(context.getUserName(), objectUF));
//						}
//					}

				}
			} else
			{
				// 无输出对象，直接提交成功
				object.setFieldValue("TSTATE", "D");
				object.setFieldValue("SBTIME", SystemTime.getTimestamp());
				object.save(context);
			}
			SendMessageUtils.sendToUser(
					EndpointEnum.RefreshRead, context, objects, "notice", "有新的任务待你确认", "有新的任务待你确认，任务编号："
							+ object.getFieldValueToString("NO") + "，任务名称为：" + object.getFieldValueToString("NAME"),
					context.getUserName(), object.getLeader());
		}
		return nameList.size() > 0 ? TaskActionResult.getResult(null, "NoOutput_Submit_Task", "D")
				: TaskActionResult.getResult(null, "Submit_Task_Successfully", "D");
	}

	/**
	 * @param taskNo 不合格汇报问题编号
	 * @return
	 */
	private USCObject[] queryUnqualified(String taskNo)
	{
		USCObject[] objects = USCObjectQueryHelper.getObjectsByCondition("CORRECTION_PREVENTION",
				"del=0 AND EXISTS(SELECT 1 FROM UNQUALIFIED A,UNQUALIFIED_CORRECTION B WHERE A.del=0 AND B.del=0 AND B.itemaid = A.id AND B.itembid = CORRECTION_PREVENTION.id AND A.NO='"
						+ taskNo + "')");

		return objects;
	}

	/**
	 * 遍历输出对象是否都有输出数据
	 *
	 * @param outObj 输出对象
	 * @param taskId 任务id
	 * @return boolean
	 */
	private boolean hasOutputBusinessItems(USCObject[] outObj, String taskId)
	{
		for (USCObject out : outObj)
		{
			// 判断每个输出对象是否有输出数据
			USCObject[] outs = TaskUtil.getOutputBusinessItems(out.getID(), taskId);
			if (outs == null)
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean disable() throws Exception
	{
		USCObject[] objects = context.getSelectObjs();
		for (USCObject uscObject : objects)
		{
			TaskObject object = (TaskObject) uscObject;
			if (!(object.getTaskState().equals("C") || object.getTaskState().equals("E")))
			{
				return true;
			}
			if (!object.getExecutor().equals(context.getUserInformation().getUserName()))
			{
				return true;
			}
		}
		return false;
	}

}
