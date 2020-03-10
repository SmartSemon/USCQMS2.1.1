package com.usc.app.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.usc.obj.api.InvokeContext;
import com.usc.server.md.ItemInfo;
import com.usc.server.md.ItemPage;
import com.usc.server.md.ItemPageField;
import com.usc.server.md.ItemPeptidePage;
import com.usc.util.ObjectHelperUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionParamParser {
	public static final String PROPERTYNO = "PROPERTYNO";
	public static final String ITEMNO = "ITEMNO";
	public static final String RELATIONPAGENO = "RELATIONPAGENO";

	public ActionParamParser()
	{
	}

	public static String getPageID(String paramString) {
		return getCfgValue(paramString, "PAGEID");
	}

	public static String getObjType(String paramString) {
		String str = getCfgValue(paramString, "OBJID");
		if (str == null)
		{ str = paramString; }
		return str;
	}

	public static String putParam(String paramString1, String paramString2, String paramString3) {
		if ((paramString1 == null) || (paramString1.length() == 0))
			return paramString2 = paramString2 + "=" + paramString3;
		paramString2 = paramString2 + "=";
		int i = paramString1.indexOf(paramString2);
		String str = null;
		if (i >= 0)
		{
			int j = paramString1.indexOf(";", i);
			if (j < 0)
			{ j = paramString1.length(); }
			str = paramString1.substring(0, i) + paramString2 + paramString3 + paramString1.substring(j);
		} else
		{
			str = paramString1 + ";" + paramString2 + paramString3;
		}
		return str;
	}

	public static String getCfgValue(String paramString1, String paramString2) {
		if ((paramString1 == null) || (paramString1.equals("")))
			return null;
		paramString2 = paramString2 + "=";
		int i = paramString1.indexOf(paramString2);
		if (i >= 0)
		{
			int j = paramString1.indexOf(";", i);
			if (j < 0)
			{ j = paramString1.length(); }
			return paramString1.substring(i + paramString2.length(), j);
		}
		return null;
	}

	public static String getCommand(String paramString) {
		return getClassName(paramString);
	}

	public static String getClassName(String paramString) {
		if (paramString == null)
		{ return null; }
		int i = paramString.indexOf("(");
		int j = paramString.lastIndexOf(")");
		if ((i < 0) || (j < 0))
		{ return paramString; }
		return paramString.substring(0, i);
	}

	public static String getParamter(String paramString) {
		if (paramString == null)
		{ return null; }
		int i = paramString.indexOf("(");
		int j = paramString.lastIndexOf(")");
		String str = null;
		if ((i >= 0) && (j > 0))
		{ str = paramString.substring(i + 1, j); }
		return str;
	}

	public static Map<String, Object> getFieldVaues(ItemInfo itemInfo, ItemPage itemPage, Map<String, Object> data) {

		if (itemPage == null)
		{
			log.warn(">>>>>> Modify object :" + itemInfo.getName() + " is not passed to the object property page");
			return data;
		}
		Map<String, Object> newData = new HashMap<String, Object>();
		if (!ObjectHelperUtils.isEmpty(data))
		{
			Integer pit = itemPage.getPeptide() == null ? 0 : itemPage.getPeptide();
			if (pit == 1)
			{
				List<ItemPeptidePage> itemPeptidePages = itemPage.getPeptidePageList();
				for (ItemPeptidePage itemPeptidePage : itemPeptidePages)
				{
					for (ItemPageField field : itemPeptidePage.getPageFieldList())
					{ putFieldData(field, data, newData); }
				}
			} else
			{
				for (ItemPageField field : itemPage.getPageFieldList())
				{ putFieldData(field, data, newData); }
			}
			if (!ObjectHelperUtils.isEmpty(data))
			{
				for (Object k : data.keySet())
				{
					if (itemInfo.containsField((String) k))
					{ newData.put((String) k, data.get(k)); }
				}
			}
		}

		return newData;
	}

	public static void putFieldData(ItemPageField field, Map<String, Object> data, Map<String, Object> newData) {
		if (field.getType() != 0)
		{
			String fNo = field.getNo();
			Object object = data.get(fNo);
			if (object != null)
			{
				newData.put(fNo, object);
			} else
			{

			}
			data.remove(fNo);
		}
	}

	public static void putCreateParam(InvokeContext paramInvokeContext, String paramString) {
//    paramInvokeContext.setPageID(getPageID(paramString));
//    String str = getObjType(paramString);
//    if ((str == null) || (str.length() == 0)) {
//      str = paramString;
//    }
//    paramInvokeContext.setObjType(str);
	}
}
