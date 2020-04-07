package com.usc.app.action.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.usc.app.entry.ret.RetSignEnum;
import com.usc.obj.api.USCObject;
import com.usc.util.ObjectHelperUtils;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ActionMessage implements ResultMessage {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private boolean flag;
	private String info;
	private String sign;
	private String errCode;
	private List<Object> dataList;

	public ActionMessage()
	{
	}

	public static ActionMessage creator(boolean flag, RetSignEnum sign, String info, Object dataList,
			String... errCode) {
		return new ActionMessage(flag, sign, info, dataList, errCode);
	}

	/**
	 * @param flag     事件操作成功与否
	 * @param sign     事件特殊标记
	 * @param info     事件处理结束提示语
	 * @param errCode  若事件执行异常或错误返回对应的错误状态码
	 * @param dataList 事件执行完成后需要返回列表数据
	 */
	public ActionMessage(boolean flag, RetSignEnum sign, String info, String... errCode)
	{
		this.flag = flag;
		this.info = info;
		if (sign != null)
		{ this.sign = sign.code; }
		if (ObjectHelperUtils.isNotEmpty(errCode))
		{ this.errCode = errCode.toString(); }
	}

	/**
	 * @param flag     事件操作成功与否
	 * @param sign     事件特殊标记
	 * @param info     事件处理结束提示语
	 * @param errCode  若事件执行异常或错误返回对应的错误状态码
	 * @param dataList 事件执行完成后需要返回列表数据
	 */
	public ActionMessage(boolean flag, RetSignEnum sign, String info, Object dataList, String... errCode)
	{
		this.flag = flag;
		this.info = info;
		if (sign != null)
		{ this.sign = sign.code; }
		if (ObjectHelperUtils.isNotEmpty(errCode))
		{ this.errCode = errCode.toString(); }
		setDataList(dataList);
	}

	public List<Object> getDataList() {
		return dataList;
	}

	public void setDataList(Object objs) {
		if (ObjectHelperUtils.isNotEmpty(objs))
		{
			this.dataList = new ArrayList<Object>();
			if (objs.getClass().isArray())
			{
				Object[] objects = (Object[]) objs;
				if (objects[0] instanceof USCObject)
				{
					for (Object object : objects)
					{ dataList.add(((USCObject) object).getFieldValuesJSON(true)); }
					return;
				}
			}
			if (objs instanceof Collection)
			{
				List objects = (List) objs;
				if (objects.get(0) instanceof USCObject)
				{
					for (Object object : objects)
					{ dataList.add(((USCObject) object).getFieldValuesJSON(true)); }
					return;
				}
				this.dataList = objects;
				return;
			}
			if (objs instanceof USCObject)
			{
				this.dataList.add(((USCObject) objs).getFieldValuesJSON(true));
				return;
			}
			this.dataList.add(objs);

		}
	}
}
