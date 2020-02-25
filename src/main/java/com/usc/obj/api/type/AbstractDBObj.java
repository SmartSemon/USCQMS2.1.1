package com.usc.obj.api.type;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import com.usc.app.action.cache.OperationalCach;
import com.usc.app.exception.ThrowException;
import com.usc.obj.api.InvokeContext;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.api.impl.USCServerBeanProvider;
import com.usc.obj.api.impl.a.AbstractUSCObjectImpl;
import com.usc.server.md.field.FieldNameInitConst;
import com.usc.util.ObjectHelperUtils;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class AbstractDBObj extends AbstractUSCObjectImpl
{

	/**
	 *
	 */
	private static final long serialVersionUID = 612946830691331092L;
	private Map<String, Object> m_Data = null;

	public AbstractDBObj(String objType, HashMap<String, Object> map)
	{
		super(objType, map);
	}

	public AbstractDBObj(String objType)
	{
		super(objType);
	}

	@Override
	public boolean save(InvokeContext context)
	{
		String itemNo = getItemNo();
		Map<String, Object> uMap = new HashMap<String, Object>();
		uMap.putAll(getModifyData());

		if (ObjectHelperUtils.isEmpty(uMap))
		{
			return true;
		}

		if (OperationalCach.isDeleted(this))
		{
			ThrowException
					.throwRemoteExceptionDetails(new RuntimeException("this USCObject is deleted,modification failed"));
			return false;
		}
		String id = this.getID();
		removeSFields(uMap);
		boolean flag = false;
		Map<String, Object> newData = null;
		try
		{
			flag = USCServerBeanProvider.getItemBean().saveDataItem(context.getCurrUserName(), getItemNo(), id, uMap);
			if (flag)
			{
				newData = USCServerBeanProvider.getItemBean().getItemInfo(itemNo, id);
				if (newData != null)
				{
					this.fieldValues.putAll(newData);
				}
				return true;
			}
		} catch (RemoteException e)
		{
			e.printStackTrace();
		} finally
		{
			if (newData != null)
			{
				USCServerBeanProvider.getSystemLogger().writeModifyItemLog((ApplicationContext) context, this, uMap);
			}
		}
		return flag;
	}

	protected void removeSFields(Map<String, Object> uMap)
	{
		uMap.remove(FieldNameInitConst.FIELD_ID);
		uMap.remove(FieldNameInitConst.FIELD_DEL);
//		uMap.remove(FieldNameInitConst.FIELD_DSNO);
		uMap.remove(FieldNameInitConst.FIELD_OWNER);
		uMap.remove(FieldNameInitConst.FIELD_CUSER);
		uMap.remove(FieldNameInitConst.FIELD_CTIME);
		uMap.remove(FieldNameInitConst.FIELD_DUSER);
		uMap.remove(FieldNameInitConst.FIELD_DTIME);
//		uMap.remove(FieldNameInitConst.FIELD_STATE);
	}

	public Map<String, Object> getModifyData()
	{
		if (this.m_Data == null)
		{
			m_Data = super.uMap;
		}
		return this.m_Data;
	}

	public void putModifyData(String f, Object v)
	{
		if (m_Data == null)
		{
			m_Data = new HashMap<String, Object>();
			m_Data.putAll(this.fieldValues);
		}
		m_Data.put(f, v);

	}

	@Override
	public boolean isDeleted(InvokeContext context) throws Exception
	{
		if (OperationalCach.isDeleted(this))
		{
			return true;
		}
		if (this.fieldValues != null)
		{
			return getFieldValueToBoolen(FieldNameInitConst.FIELD_DEL);
		}
		return false;
	}

	@Override
	public boolean isDeleteAble(InvokeContext context) throws Exception
	{
		return super.isDeleteAble(context);
	}

	public void setObj2Deleted()
	{
		this.fieldValues.put(FieldNameInitConst.FIELD_DEL, true);
	}

	@Override
	public boolean canClone(InvokeContext context) throws Exception
	{
		return false;
	}

	@Override
	public USCObject cloneObj(InvokeContext context) throws Exception
	{
		return null;
	}

	@Override
	public boolean isDisplayable(InvokeContext paramInvokeContext)
	{
		return false;
	}

}
