package com.usc.obj.api.type.task.i;

import com.usc.obj.api.USCObject;

public interface TaskRelationInfo
{
	public abstract USCObject[] getTaskInputBusinessItems();

	public abstract USCObject[] getTaskInputObjs();

	public abstract USCObject[] getTaskOutputObjs();

	public abstract USCObject[] addOutPutModelItems(String... itemNos);

	public abstract USCObject[] addOutPutBusinessObjects(USCObject inItem, USCObject... objects);

	public abstract USCObject[] addInPutModelItems(String... itemNos);

	public abstract USCObject[] addInPutBusinessObjects(USCObject inItem, USCObject... objects);
}
