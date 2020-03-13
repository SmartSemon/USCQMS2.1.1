package com.usc.app.query.search;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.query.search.i.AbstractSearchAction;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.obj.api.USCObject;
import com.usc.obj.util.USCObjectQueryHelper;
import com.usc.server.md.ItemField;
import com.usc.server.md.field.FieldAdapter;
import com.usc.util.ObjectHelperUtils;

public class AdvancedSearchObjAction extends AbstractAction implements AbstractSearchAction {

	@Override
	public Object executeAction() throws Exception {
		String queryWord = parsingQueryWord();
		if (ObjectHelperUtils.isNotEmpty(queryWord))
		{
			String condition = "";
			String cond = getCondition(context).toUpperCase();
			String s1 = cond.substring(0, cond.indexOf("ORDER"));
			String s2 = getSortFields(context);
			if (ObjectHelperUtils.isNotEmpty(s1.replace(" ", "")))
			{
				condition = s1 + queryWord + s2;
			} else
			{
				condition = queryWord.substring(4, queryWord.length()) + s2;
			}
			USCObject[] objects = USCObjectQueryHelper.getObjectsByCondition(context.getItemNo(), condition);
			return ActionMessage.creator(true, RetSignEnum.QUERY,
					StandardResultTranslate.translate("Action_AdvancedSearch_1"), objects);
		}

		return ActionMessage.creator(false, RetSignEnum.QUERY,
				StandardResultTranslate.translate("Action_AdvancedSearch_1"), null);
	}

	private String parsingQueryWord() {
		List<ItemField> queryFields = context.getItemInfo().getSupQueryFieldList();
		String qw = getQueryWord(context);
		JSONObject jsonObject = JSONObject.parseObject(qw);
		StringBuffer buffer = new StringBuffer();
		for (ItemField itemField : queryFields)
		{
			String fn = itemField.getFieldName();
			String ft = itemField.getFType();
			Object word = jsonObject.get(fn);
			if (ObjectHelperUtils.isNotEmpty(word))
			{
				String cnd = parsingFV(fn, ft, word);
				buffer.append(cnd);
			}
		}
		return buffer.toString();
	}

	private String parsingFV(String fn, String ft, Object word) {
		String cond = " and ";
		if (ft.equals(FieldAdapter.FIELD_TYPE_DATETIME))
		{
			JSONArray array = JSONArray.parseArray(word.toString());
			if (ObjectHelperUtils.isNotEmpty(array))
			{
				String stime = array.getString(0);
				String etime = array.getString(1);
				return cond + fn + " BETWEEN '" + stime + "' and '" + etime + "'";
			}

		} else
		{
			return cond + fn + " LIKE '%" + word + "%'";
		}
		return "";
	}

	@Override
	public boolean disable() throws Exception {
		return false;
	}

}
