package com.usc.obj.api.type.task;

import java.util.Date;
import java.util.HashMap;

import com.usc.app.action.cache.OperationalCach;
import com.usc.app.action.mate.MateFactory;
import com.usc.obj.api.InvokeContext;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.USCObjectAction;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.api.impl.USCServerBeanProvider;
import com.usc.obj.api.type.GeneralObject;
import com.usc.obj.api.type.task.i.TaskRelationInfo;
import com.usc.server.md.ItemInfo;

public class TaskObject extends GeneralObject implements TaskRelationInfo
{

	/**
	 *
	 */
	private static final long serialVersionUID = -5616391473219609524L;

	public TaskObject(String objType, HashMap<String, Object> map)
	{
		super(objType, map);
	}

	@Override
	public boolean delete(InvokeContext context) throws Exception
	{
		if (!isDeleteAble(context))
		{
			return false;
		}
		boolean b = false;
		if (!isDeleted(context))
		{
			USCObjectAction objectAction = context.getActionObjType();
			objectAction.setCurrObj(this);
			b = objectAction.delete(context);
			if (b)
			{
				setObj2Deleted();
				OperationalCach.putDeleteObj(this);
			}
		}

		return b;
	}

	@Override
	public boolean isDeleteAble(InvokeContext context) throws Exception
	{
		return isEnable() && super.isDeleteAble(context);
	}

	public boolean isEnable()
	{
		boolean b = false;
		String tstate = getTaskState();
		if ("A".equals(tstate) || "E".equals(tstate))
		{
			b = true;
		}
//		else
//		{
//			LoggerFactory.logError("Task state is not 'A' or 'E', do not operate this task");
//		}
		return b;
	}

	@Override
	public boolean save(InvokeContext context)
	{
		boolean b = false;
		StackTraceElement[] s = new Exception().getStackTrace();
		if (s[1].getClassName().equals("com.usc.app.action.BatchModifyAction"))
		{
			b = isEnable() ? super.save(context) : false;
		} else
		{
			b = super.save(context);
		}
		if (this.getTaskState().equals("F") || this.getTaskState().equals("Z"))
		{
			b = false;
		}
		return b;
	}

	public String getTaskState()
	{
		Object task_status = this.getFieldValue("TSTATE");
		String tstate = String.valueOf(task_status);
		return tstate;
	}

	public String getgetTaskStateToString()
	{
		return this.getFieldValueToString("TSTATE");
	}

	public String getTaskType()
	{
		return this.getFieldValueToString("TTYPE");
	}

	public String getGrade()
	{
		return this.getFieldValueToString("TGRADE");
	}

	public Date getETime()
	{
		return (Date) this.getFieldValue("ETIME");
	}

	public String getLeader()
	{
		return this.getFieldValueToString("LEADER");
	}

	public String getExecutor()
	{
		return this.getFieldValueToString("EXECUTOR");
	}

	public String getDLUser()
	{
		return this.getFieldValueToString("DLUSER");
	}

	public Date getDLTime()
	{
		return (Date) this.getFieldValue("DLTIME");
	}

	public String getSTUser()
	{
		return this.getFieldValueToString("STUSER");
	}

	public Date getSTTime()
	{
		return (Date) this.getFieldValue("STTIME");
	}

	public Date getFNSTime()
	{
		return (Date) this.getFieldValue("FNSTIME");
	}

	public Date getCFTime()
	{
		return (Date) this.getFieldValue("CFTIME");
	}

	@Override
	public USCObject[] getTaskInputBusinessItems()
	{
		return TaskUtil.getInputBusinessItems(this.getID());
	}

	@Override
	public USCObject[] getTaskInputObjs()
	{

		return TaskUtil.getInputs(this.getID());
	}

	@Override
	public USCObject[] getTaskOutputObjs()
	{
		return null;
	}

	@Override
	public USCObject[] addOutPutModelItems(String... itemNos)
	{
		if (itemNos != null)
		{
			for (String itemNo : itemNos)
			{
				ItemInfo outInfo = MateFactory.getItemInfo(itemNo);
				if (outInfo != null)
				{
					String name = outInfo.getName();
					HashMap<String, Object> hashMap = new HashMap<String, Object>();
					hashMap.put("NO", itemNo);
					hashMap.put("NAME", name);
					hashMap.put("ITEMNO", itemNo);
//					hashMap.put("ITEMTYPE", inputs[i].getFieldValue("ITEMTYPE"));
					hashMap.put("PID", "0");
					hashMap.put("TASKID", this.getID());
					ApplicationContext context = (ApplicationContext) USCServerBeanProvider.getContext("TASKOUTPUT",
							this.getCreateUser());
					context.setFormData(hashMap);
					try
					{
						context.createObj(context.getItemNo());
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	@Override
	public USCObject[] addOutPutBusinessObjects(USCObject outItem, USCObject... objects)
	{
		ApplicationContext context = (ApplicationContext) USCServerBeanProvider.getContext("TASKOUTPUT",
				this.getCreateUser());
		for (USCObject uscObject : objects)
		{
			String fieldNo = "NO";
			String fieldName = "NAME";
			if ("CORRECTION_PREVENTION".contentEquals(uscObject.getItemNo()))
			{
				fieldName = "MEASURES";
			}
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("NO", uscObject.getFieldValueBykey(fieldNo));
			hashMap.put("NAME", uscObject.getFieldValueBykey(fieldName));
			hashMap.put("OBJECTID", uscObject.getID());
			hashMap.put("ITEMNO", outItem.getFieldValueToString("ITEMNO"));
//			hashMap.put("ITEMTYPE", uscObject.geti);
			hashMap.put("PID", outItem.getID());
			hashMap.put("TASKID", this.getID());
			context.setFormData(hashMap);
			try
			{
				context.createObj(context.getItemNo());
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public USCObject[] addInPutModelItems(String... itemNos)
	{
		USCObject[] objects = null;
		if (itemNos != null)
		{
			objects = new USCObject[itemNos.length];
			int i = 0;
			for (String itemNo : itemNos)
			{
				ItemInfo outInfo = MateFactory.getItemInfo(itemNo);
				if (outInfo != null)
				{
					String name = outInfo.getName();
					HashMap<String, Object> hashMap = new HashMap<String, Object>();
					hashMap.put("NO", itemNo);
					hashMap.put("NAME", name);
					hashMap.put("ITEMNO", itemNo);
//					hashMap.put("ITEMTYPE", inputs[i].getFieldValue("ITEMTYPE"));
					hashMap.put("PID", "0");
					hashMap.put("TASKID", this.getID());
					ApplicationContext context = (ApplicationContext) USCServerBeanProvider.getContext("TASKINPUT",
							this.getCreateUser());
					context.setFormData(hashMap);
					try
					{
						objects[i] = context.createObj(context.getItemNo());
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		return objects;
	}

	@Override
	public USCObject[] addInPutBusinessObjects(USCObject outItem, USCObject... objects)
	{
		ApplicationContext context = (ApplicationContext) USCServerBeanProvider.getContext("TASKINPUT",
				this.getCreateUser());
		for (USCObject uscObject : objects)
		{
			String fieldNo = "NO";
			String fieldName = "NAME";
			if ("UNQUALIFIED".contentEquals(uscObject.getItemNo()))
			{
				fieldName = "QPDESCRIPTION";
			}
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("NO", uscObject.getFieldValueBykey(fieldNo));
			hashMap.put("NAME", uscObject.getFieldValueBykey(fieldName));
			hashMap.put("OBJECTID", uscObject.getID());
			hashMap.put("ITEMNO", outItem.getFieldValueToString("ITEMNO"));
//			hashMap.put("ITEMTYPE", uscObject.geti);
			hashMap.put("PID", outItem.getID());
			hashMap.put("TASKID", this.getID());
			context.setFormData(hashMap);
			try
			{
				context.createObj(context.getItemNo());
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

}
