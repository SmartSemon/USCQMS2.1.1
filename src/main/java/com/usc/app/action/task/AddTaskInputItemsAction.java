package com.usc.app.action.task;

import java.util.HashMap;

import com.usc.app.action.a.AbstractRelationAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.util.tran.InternationalFormat;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.type.task.TaskInputObject;
import com.usc.obj.api.type.task.TaskUtil;
import com.usc.util.ObjectHelperUtils;

public class AddTaskInputItemsAction extends AbstractRelationAction {

	@Override
	public Object executeAction() throws Exception {
		if (!TaskUtil.checkLeader(context, root))
		{ return failedOperation(InternationalFormat.getFormatMessage("NoAuth_Received_Task", context.getLocale())); }

		if (TaskUtil.checkTaskState(context, root))
		{
			USCObject[] inputs = context.getSelectObjs();
			if (ObjectHelperUtils.isEmpty(inputs))
			{
				return failedOperation(
						InternationalFormat.getFormatMessage("No_Objects_Selected", context.getLocale()));
			}
			USCObject[] inputObjects = new TaskInputObject[inputs.length];
			for (int i = 0; i < inputs.length; i++)
			{
				HashMap<String, Object> hashMap = new HashMap<String, Object>();
				hashMap.put("NO", inputs[i].getFieldValueBykey("NO"));
				hashMap.put("NAME", inputs[i].getFieldValueBykey("NAME"));
				hashMap.put("ITEMNO", inputs[i].getFieldValueBykey("NO"));
//				hashMap.put("ITEMTYPE", inputs[i].getFieldValue("ITEMTYPE"));
				hashMap.put("PID", "0");
				hashMap.put("TASKID", root.getID());
				context.setFormData(hashMap);
				USCObject object = context.createObj(context.getItemNo());
				inputObjects[i] = object;
			}
			return new ActionMessage(flagTrue, RetSignEnum.NEW,
					InternationalFormat.getFormatMessage("BatchAdd_Success", context.getLocale()), inputObjects);
		} else
		{
			return failedOperation("任务状态为 [" + root.getFieldValueToString("TSTATE") + "]禁止操作任务输入项");
		}

	}

}
