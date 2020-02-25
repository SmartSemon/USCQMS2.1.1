package com.usc.app.view;

import com.usc.app.view.a.AbstractItemRelationView;
import com.usc.app.view.search.ClassViewNodeReplaceSql;
import com.usc.obj.api.USCObject;
import com.usc.obj.util.USCObjExpHelper;
import com.usc.obj.util.USCObjectQueryHelper;

public class RealtionClassViewGetNodeData extends AbstractItemRelationView
{

	@Override
	public Object executeAction() throws Exception
	{
		String treenodepid = classViewNode.getTreenodepid();
		String treenodedata = classViewNode.getTreenodedata();
		String dataCondition = classViewNode.getDatacondition();
		dataCondition = USCObjExpHelper.parseObjValueInExpression(root,
				ClassViewNodeReplaceSql.replaceCNodeDataSql(context, dataCondition, treenodedata, treenodepid));
//		List list = DBUtil.getSQLResultByConditionLimit(context.getItemInfo(), dataCondition, getPage());
		USCObject[] objects = USCObjectQueryHelper.getObjectsByConditionLimit(context.getItemNo(), dataCondition,
				getPage());
		return objects;
	}
}
