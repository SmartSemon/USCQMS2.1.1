package com.usc.app.query.search;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.query.search.i.AbstractSearchAction;
import com.usc.app.util.SearchUtils;
import com.usc.server.md.ItemInfo;
import com.usc.server.util.LoggerFactory;

/**
 * Class_Name: SearchSingleObjectAction.java<br>
 *
 * <p>
 * Description:查询单对象
 * </P>
 *
 * @date 2019年9月16日
 * @author SEMON
 */
public class SearchSingleObjectAction extends AbstractAction implements AbstractSearchAction
{

	@Override
	public Object executeAction() throws Exception
	{
		ItemInfo itemInfo = context.getItemInfo();
		if (itemInfo == null)
		{
			LoggerFactory.logError("Business objects noe exists");
			return failedOperation();
		}
		if (!hasQueryFields(itemInfo))
		{
			return failedOperation("Business object queryFiledes not exists");
		}
		return new ActionMessage(flagTrue, RetSignEnum.QUERY, "OK",
				SearchUtils.search(itemInfo, getQueryWord(context), getDataPage(context)));
	}

	@Override
	public boolean disable() throws Exception
	{
		return true;
	}

}
