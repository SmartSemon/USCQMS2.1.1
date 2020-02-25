package com.usc.obj.api.impl.a;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

import com.usc.app.exception.ApplicationException;
import com.usc.obj.api.InvokeContext;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.USCObjectAction;
import com.usc.obj.api.bean.AppMainCreator;
import com.usc.obj.api.bean.USCObjCreator;
import com.usc.obj.api.bean.UserInformation;
import com.usc.server.md.ItemField;
import com.usc.server.md.ItemInfo;
import com.usc.server.md.field.FieldUtils;

public abstract class DefaultInvokeContext implements InvokeContext
{

	protected USCObject[] objs = null;
	private Map<String, Object> formData = null;
	private Map extendInfo = new HashMap<String, Object>();
	protected String param = null;
	private String itemNo;
	private boolean isInitModel = true;
	private USCObjectAction objAction = null;

	public DefaultInvokeContext()
	{
	}

	public USCObject getSelectedObj()
	{
		return objs != null ? objs[0] : null;
	}

	public void setCurrObj(USCObject paramObj)
	{
		if (paramObj == null)
		{
			this.objs = null;
			return;
		}

		if (this.objs != null && this.objs.length > 0)
		{
			this.objs[0] = paramObj;
		} else
		{
			this.objs = new USCObject[]
			{ paramObj };
		}

	}

	public String getParam()
	{
		return this.param;
	}

	public void setParam(String paramString)
	{
		this.param = paramString;
	}

	public String getClientID()
	{
		UserInformation information = getUserInformation();
		if (information != null)
		{
			return information.getClientID();
		}
		return null;
	}

	public USCObject[] getSelectObjs()
	{
		return objs;
	}

	public void setSelectObjs(USCObject[] objs)
	{

		this.objs = objs;
	}

	public boolean isInitModel()
	{
		return false;
	}

	public void setInitModel(boolean paramBoolean)
	{
		this.isInitModel = paramBoolean;

	}

	public Map<String, Object> getFormData()
	{
		return this.formData;
	}

	public void setFormData(Map<String, Object> map)
	{
		this.formData = map;
	}

	public void setContextFormData(Map<String, Object> map, ItemInfo info)
	{
		if (map == null)
		{
			return;
		}
		if (info == null)
		{
			Assert.notNull(getItemNo(), "itemNo must not be null");
		}
		this.formData = new HashMap<String, Object>();
		map.forEach((field, value) -> {
			ItemField itemField = info.getItemField(field);
			if (itemField != null)
			{
				Object object = FieldUtils.getDBObjectByType(value, itemField);
				formData.put(field, object);
			}
		});
	}

	public String getItemNo()
	{
		return itemNo;
	}

	public void setItemNo(String paramString)
	{
		this.itemNo = paramString;

	}

	public USCObjectAction getActionObjType()
	{
		return objAction;
	}

	public void setActionObjType(USCObjectAction objectAction)
	{
		this.objAction = objectAction;
	}

	public void putContext(InvokeContext context)
	{

	}

	public String getCurrUserName()
	{
		UserInformation userInformation = getUserInformation();
		if (userInformation != null)
		{
			return userInformation.getUserName();
		}
		return null;
	}

	public USCObject createObj(String objType) throws Exception
	{
		setItemNo(objType);
		USCObjCreator creator = new AppMainCreator(objType);
		if (((AppMainCreator) creator).VerifyItemUniqueness(this.getFormData()))
		{
			throw new ApplicationException("违反字段唯一性约束，字段：" + creator.getOnlyFields(this));
		}
		USCObject object = creator.create(this);
		setCurrObj(object);
		return object;
	}

	@Override
	public Object getExtendInfo(Object paramObject)
	{
		return extendInfo.get(paramObject);
	}

	@Override
	public void setExtendInfo(Object paramObject1, Object paramObject2)
	{
		extendInfo.putIfAbsent(paramObject1, paramObject2);
	}

}
