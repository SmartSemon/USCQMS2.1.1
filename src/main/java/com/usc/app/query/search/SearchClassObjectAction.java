package com.usc.app.query.search;

import com.usc.app.action.a.AbstractClassAction;
import com.usc.app.exception.ThrowException;
import com.usc.app.query.search.i.AbstractSearchAction;
import com.usc.app.util.SearchUtils;
import com.usc.obj.api.type.ClassNodeObject;
import com.usc.server.md.ItemInfo;
import com.usc.server.md.field.FieldNameInitConst;

public class SearchClassObjectAction extends AbstractClassAction implements AbstractSearchAction
{

	@Override
	public Object executeAction() throws Exception
	{
		ItemInfo itemInfo = context.getItemInfo();
		if (itemInfo == null)
		{
			throw ThrowException.throwNullPointerException("Business object not exists");
		}
		if (!hasQueryFields(itemInfo))
		{
			throw ThrowException.throwNullPointerException("Business object queryField not exists");
		}
		if (getClassItemInfo() == null)
		{
			throw ThrowException.throwNullPointerException("Classification object does not exist");
		}

		ClassNodeObject nodeObj = (ClassNodeObject) super.nodeObj;
		String condition = "EXISTS(SELECT 1 FROM " + getClassItemInfo().getTableName() + " WHERE "
				+ FieldNameInitConst.FIELD_DEL + "=0 AND " + FieldNameInitConst.FIELD_ITEMID + "="
				+ itemInfo.getTableName() + "." + FieldNameInitConst.FIELD_ID + " AND "
				+ FieldNameInitConst.FIELD_NODEID + "='" + nodeObj.getID() + "')";

		return queryTrue(
				SearchUtils.searchByCondition(itemInfo, getQueryWord(context), condition, getDataPage(context)));
	}

	@Override
	public boolean disable() throws Exception
	{
		return true;
	}

}
