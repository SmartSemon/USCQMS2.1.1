package com.usc.app.action.task.query;

import com.usc.app.action.utils.ActionMessage;
import com.usc.app.query.a.AbstractQueryItemRelationDataAction;
import com.usc.obj.api.type.task.TaskUtil;

public class GetTaskInOrOutputData extends AbstractQueryItemRelationDataAction
{

	@Override
	public Object executeAction() throws Exception
	{
		return new ActionMessage(flagTrue, null, "", TaskUtil.getTaskInOrOutputData(context, root, getPage()));
	}

}
