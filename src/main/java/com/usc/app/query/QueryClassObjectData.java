package com.usc.app.query;

import com.usc.app.action.a.AbstractClassAction;
import com.usc.obj.util.USCObjectQueryHelper;

/**
 * 查询手动分类节点下数据
 *
 * @author SEMON
 *
 */
public class QueryClassObjectData extends AbstractClassAction
{

	@Override
	public Object executeAction() throws Exception
	{
		if (!super.disable())
		{
			String nodeID = nodeObj.getID();
			String itemNo = context.getItemNo();
			String classTable = super.getClassItemInfo().getTableName();
			return USCObjectQueryHelper.getClassObjects(nodeID, itemNo, classTable);
//			List dataList = ItemUtiilities.getClasObjectResult(nodeID, itemNo, classTable);
//			return this.queryTrue(dataList);
		}
		return this.queryFalse(null);
	}

}
