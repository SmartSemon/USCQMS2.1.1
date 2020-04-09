package com.usc.app.action.demo.zc;

import java.util.Map;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.action.task.ReleaseTaskAction;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.api.impl.USCServerBeanProvider;
import com.usc.obj.api.type.task.TaskObject;
import com.usc.obj.util.USCObjectQueryHelper;

/**
 * @author SEMON
 *         <p>
 *         批量确认责任判定
 *         </p>
 */
public class ConfirmationLiabilityAction extends AbstractAction
{

	@Override
	public Object executeAction() throws Exception
	{
		Map<String, Object> taskData = context.getFormData();
		taskData.put("TSTATE", "A");
		ApplicationContext applicationContext = (ApplicationContext) USCServerBeanProvider.getContext("TASK",
				context.getUserName());
		applicationContext.setFormData(taskData);
		TaskObject task = (TaskObject) applicationContext.createObj("TASK");
		String taskNo = task.getNo();
		USCObject objectUF = USCObjectQueryHelper.getObjectByCondition("UNQUALIFIED", "del=0 AND no='" + taskNo + "'");
		if (objectUF != null)
		{
			USCObject inItem = task.addInPutModelItems("UNQUALIFIED")[0];
			task.addInPutBusinessObjects(inItem, objectUF);
		}

		task.addOutPutModelItems("CORRECTION_PREVENTION");
		ReleaseTaskAction releaseTaskAction = new ReleaseTaskAction();
		releaseTaskAction.setApplicationContext(applicationContext);
		releaseTaskAction.action();
		USCObject object = context.getSelectedObj();
		context.setItemNo("UNQUALIFIED");
		object.setFieldValue("DWSTATE", "D");
		object.save(context);
		return new ActionMessage(flagTrue, RetSignEnum.MODIFY, "接受判定责任成功", object);
	}

	@Override
	public boolean disable() throws Exception
	{
		String userName = context.getUserName();
		USCObject[] objects = context.getSelectObjs();
		for (USCObject uscObject : objects)
		{
			String dwState = (String) uscObject.getFieldValue("DWSTATE");
			String type = (String) uscObject.getFieldValue("TYPE");
			String pjuser = uscObject.getFieldValueToString("PJUSER");
			if (!"B".equals(dwState) || !userName.equals(pjuser) || "A".equals(type))
			{
				return true;
			}
		}
		return false;
	}

}
