package com.usc.obj.api;

import java.util.Locale;
import java.util.Map;

public abstract interface InvokeContext extends USCObjectProvider, UserInfoProvider {
	public abstract String getParam();

	public abstract void setParam(String string);

	public abstract String getClientID();

	public abstract USCObject[] getSelectObjs();

	public abstract void setSelectObjs(USCObject[] obs);

	public abstract Object getExtendInfo(Object object);

	public abstract void setExtendInfo(Object object, Object object2);

	public abstract boolean isInitModel();

	public abstract void setInitModel(boolean b);

	public abstract Map<String, Object> getFormData();

	public abstract void setFormData(Map<String, Object> map);

	public abstract String getItemNo();

	public abstract void setItemNo(String string);

	public abstract USCObjectAction getActionObjType();

	public abstract void setActionObjType(USCObjectAction objectAction);

	public abstract USCObject createObj(String string) throws Exception;

	public abstract InvokeContext cloneContext();

	public abstract String getCurrUserName();

	public abstract Locale getLocale();

//	public abstract USCModelMate getMetadataParser();
}
