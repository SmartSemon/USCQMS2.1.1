package com.usc.app.view;

import com.usc.app.view.a.AbstractItemView;
import com.usc.app.view.search.ClassViewNodeReplaceSql;
import com.usc.obj.api.USCObject;
import com.usc.obj.util.USCObjectQueryHelper;

public class ClassViewGetNodeData extends AbstractItemView
{

	@Override
	public Object executeAction() throws Exception
	{
		String treenodepid = classViewNode.getTreenodepid();
		String treenodedata = classViewNode.getTreenodedata();
		String dataCondition = classViewNode.getDatacondition();
		dataCondition = ClassViewNodeReplaceSql.replaceCNodeDataSql(context, dataCondition, treenodedata, treenodepid);
		USCObject[] objects = USCObjectQueryHelper.getObjectsByConditionLimit(context.getItemNo(), dataCondition,
				getPage());
		return objects;
	}

	@Override
	public boolean disable() throws Exception
	{
		return true;
	}
}
