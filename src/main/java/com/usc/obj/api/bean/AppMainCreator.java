package com.usc.obj.api.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.usc.app.action.mate.MateFactory;
import com.usc.obj.api.InvokeContext;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.api.impl.USCServerBeanProvider;
import com.usc.obj.util.USCObjectQueryHelper;
import com.usc.server.md.ItemField;
import com.usc.server.md.ItemInfo;
import com.usc.server.md.USCModelMate;
import com.usc.server.syslog.LOGActionEnum;

public class AppMainCreator implements USCObjCreator
{
	private String itemNo = null;
	private Map<String, Object> initData = new HashMap<String, Object>();
	private boolean only = false;

	public AppMainCreator(String objType)
	{
		this.itemNo = objType;
	}

	@Override
	public USCObject create(InvokeContext context) throws Exception
	{

		if (!createAble(context))
		{
			return null;
		}
		HashMap<String, Object> map = (HashMap<String, Object>) USCServerBeanProvider.getItemBean().newItem(itemNo,
				context.getUserInformation().getUserName(), this.initData);
		USCObject uscObject = null;
		if (context instanceof ApplicationContext)
		{
			ApplicationContext applicationContext = (ApplicationContext) context;
			ItemInfo info = applicationContext.getItemInfo();
			uscObject = USCObjectQueryHelper.getObjectByID(info.getItemNo(), (String) map.get("ID"));
			if (uscObject != null)
			{
				USCServerBeanProvider.getSystemLogger().writeNewItemLog(applicationContext, uscObject,
						LOGActionEnum.NEW);
			}
		}

		return uscObject;
	}

	@Override
	public boolean createAble(InvokeContext context) throws Exception
	{
		if (itemNo == null)
		{
			this.itemNo = context.getItemNo();
		}
		this.initData = context.getFormData();
		if (this.itemNo == null || this.initData == null)
		{
			return false;
		}
		return true;
	}

	public boolean VerifyItemUniqueness(Map<String, Object> newData)
	{
		try
		{
			ItemInfo itemInfo = MateFactory.getItemInfo(itemNo);
			setOnly(USCModelMate.VerifyItemUniqueness(itemInfo, newData));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return only;

	}

	public boolean isOnly()
	{
		return only;
	}

	public void setOnly(boolean only)
	{
		this.only = only;
	}

	@Override
	public String getOnlyFields(InvokeContext context) throws Exception
	{
		ItemInfo info = MateFactory.getItemInfo(context.getItemNo());
		List<ItemField> fields = USCModelMate.getOnlyItemFields(info);
		if (fields == null)
		{
			return null;
		}
		StringBuffer fieldName = new StringBuffer("[ ");
		for (int i = 0; i < fields.size(); i++)
		{
			if (i > 0)
			{
				fieldName.append(",");
			}
			fieldName.append(fields.get(i).getName());
		}
		fieldName.append(" ]");
		return fieldName.toString();
	}

	public static List<ItemField> getItemOnlyFields(String itemNo) throws Exception
	{
		ItemInfo info = MateFactory.getItemInfo(itemNo);
		List<ItemField> fields = USCModelMate.getOnlyItemFields(info);
		return fields;
	}

}
