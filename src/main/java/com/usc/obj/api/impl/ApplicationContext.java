package com.usc.obj.api.impl;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.usc.app.action.cache.OperationalCach;
import com.usc.app.action.mate.MateFactory;
import com.usc.app.sys.online.OnlineUsers;
import com.usc.app.util.UserInfoUtils;
import com.usc.obj.api.InvokeContext;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.USCObjectAction;
import com.usc.obj.api.bean.UserInformation;
import com.usc.obj.api.impl.a.DefaultInvokeContext;
import com.usc.obj.jsonbean.ActionRequestJSONBean;
import com.usc.obj.jsonbean.JSONBean;
import com.usc.obj.util.NewUSCObjectHelper;
import com.usc.server.md.ItemInfo;
import com.usc.server.md.ItemPage;
import com.usc.util.ObjectHelperUtils;

import lombok.Data;

@Data
public class ApplicationContext extends DefaultInvokeContext {
	private JSONBean bean;
	private String userName;
	private HttpServletRequest servletRequest;
	private HttpServletResponse servletResponse;

	public ApplicationContext(JSONBean jsonBean)
	{
		this.bean = jsonBean;
	}

	public ApplicationContext(String userName, USCObject... currObj)
	{
		this(currObj[0].getItemNo(), userName);
		setSelectObjs(currObj);
		setCurrObj(currObj[0]);
	}

	public ApplicationContext(String itemNo, String userName)
	{
		setItemNo(itemNo);
		setItemInfo(MateFactory.getItemInfo(itemNo));
		setUserName(userName);
	}

	public void setContext() {
		String itemNo = bean.getItemNo();
		if (itemNo == null)
		{
			String classItemNo = bean.getClassNodeItemNo();
			itemNo = classItemNo != null ? classItemNo : null;
		}

		Assert.notNull(itemNo, "itemNo must not be null");
		setItemNo(itemNo);

		setItemInfo(MateFactory.getItemInfo(getItemNo()));
		HashMap<String, Object> data = bean.getData();
		Map<String, Object> initData = getFormData();
		if (ObjectHelperUtils.isEmpty(initData))
		{
			initData = data;
		} else
		{
			initData.putAll(data);
		}
		setFormData(initData);

		List<HashMap<String, Object>> hData = bean.getHData();
		if (ObjectHelperUtils.isEmpty(hData))
		{
			HashMap<String, Object> classNodeData = bean.getClassNodeData();
			if (ObjectHelperUtils.isNotEmpty(classNodeData))
			{
				hData = new Vector<HashMap<String, Object>>();
				hData.add(classNodeData);
			}
		}
		if (!ObjectHelperUtils.isEmpty(hData))
		{
			String objectType = null;
			setBean(bean);
			USCObject[] objects = new USCObject[hData.size()];
			for (int i = 0; i < hData.size(); i++)
			{
				Map<String, Object> hMap = hData.get(i);
				if (OperationalCach.isMData(hMap.get("ID")))
				{
					objects[i] = OperationalCach.getMData((String) hMap.get("ID"));
				} else
				{
					objectType = (String) hMap.get("USC_OBJECT");
					objects[i] = NewUSCObjectHelper.newObject(objectType == null ? getItemNo() : objectType, hMap);
				}

			}
			this.setSelectObjs(objects);
			USCObjectAction objectAction = (USCObjectAction) ObjectCachingDataHelper
					.newInstance(MateFactory.getItemInfo(itemNo).getImplClass() + "Action");
			setActionObjType(objectAction);
		}
		setEX(bean);
	}

	@Override
	public void setFormData(Map<String, Object> map) {
		super.setContextFormData(map, this.itemInfo);
	}

	public String getModelNo() {
		return this.bean.getMNo();
	}

	private void setBean(JSONBean bean) {
		this.bean = bean;
	}

	private ItemInfo itemInfo;

	public ItemInfo getItemInfo() {
		if (itemInfo == null)
		{ return MateFactory.getItemInfo(getItemNo()); }
		return itemInfo;
	}

	private void setItemInfo(ItemInfo itemInfo) {
		this.itemInfo = itemInfo;
	}

	private void setEX(JSONBean bean) {
		String username = bean.getUserName();
		setUserName(username);

		Field[] fields = JSONBean.class.getDeclaredFields();
		for (Field field : fields)
		{
			String key = field.getName();
			Object object = getResult(key, bean);
			if (key.equals("otherParam") && object != null)
			{
				String o = String.valueOf(object);
				if (o.startsWith("["))
				{
					JSONArray array = JSONArray.parseArray(o);
					setExtendInfo(key, array);
				} else
				{
					JSONObject jsonObject = JSONObject.parseObject(o);
					jsonObject.forEach((k, v) -> setExtendInfo(k, v));
				}
				continue;
			}
			setExtendInfo(key, object);
		}
	}

	private Object getResult(Object fieldName, JSONBean bean) {
		try
		{
			Class<?> aClass = JSONBean.class;
			Field declaredField = aClass.getDeclaredField(fieldName.toString());
			declaredField.setAccessible(true);
			PropertyDescriptor pd = new PropertyDescriptor(declaredField.getName(), aClass);
			Method readMethod = pd.getReadMethod();

			return readMethod.invoke(bean);
		} catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		} catch (IntrospectionException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public ItemPage getItemPage() {
		String pageNo = bean.getMNo();
		if (bean instanceof ActionRequestJSONBean)
		{
			ActionRequestJSONBean requestJSONBean = (ActionRequestJSONBean) bean;
			return requestJSONBean.getItemPage(pageNo);
		} else
		{
			return MateFactory.getItemInfo(bean.getItemNo()).getItemPage(pageNo);
		}

	}

	@Override
	public InvokeContext cloneContext() {
		ApplicationContext applicationContext = new ApplicationContext(getUserName(), getSelectObjs());
		applicationContext.setFormData(getFormData());
		applicationContext.setActionObjType(getActionObjType());
//		applicationContext.setSelectObjs(getSelectObjs());
//		applicationContext.setCurrObj(getSelectedObj());
//		applicationContext.setItemNo(getItemNo());
//		applicationContext.setUserName(getUserName());
		return applicationContext;
	}

	@Override
	public UserInformation getUserInformation() {
		return createUserInformation();
	}

	@Override
	public UserInformation createUserInformation() {
		return UserInfoUtils.getUserInformation(getUserName());
	}

	@Override
	public UserInformation saveUserInformation() {
		return null;
	}

	@Override
	public UserInformation deleteUserInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserClientIP() {
		try
		{
			return OnlineUsers.getOnUser(getUserName()).getIp();
		} catch (Exception e)
		{
			return null;
		}

	}

}
