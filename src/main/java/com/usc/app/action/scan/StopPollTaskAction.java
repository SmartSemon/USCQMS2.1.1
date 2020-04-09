package com.usc.app.action.scan;

import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.util.http.HttpURLConnectionUtils;
import com.usc.obj.api.USCObject;
import com.usc.server.util.SystemTime;

@RestController
public class StopPollTaskAction extends AbstractAction
{

	@Override
	public Object executeAction() throws Exception
	{
		USCObject object = context.getSelectedObj();
		String clazz = object.getFieldValueToString("IMPLCLASS");
		if (clazz != null)
		{
			String url = "http://localhost:8899/poll/stop";
			Map<String, Object> impl = new JSONObject();
			impl.put("IMPLCLASS", clazz);
			String resultString = HttpURLConnectionUtils.sendPostRequest(url, impl, context.getClientID());
			if ("true".equals(resultString))
			{
				object.setFieldValue("ISENABLE", false);
				object.setFieldValue("STOPTIME", SystemTime.getTimestamp());
				object.save(context);
				return new ActionMessage(flagTrue, RetSignEnum.MODIFY, "重启轮询任务成功", object);
			}
		}
		return failedOperation();
	}

	@Override
	public boolean disable() throws Exception
	{
		// 需要验证用户是否为系统管理员
		USCObject[] objects = context.getSelectObjs();
		for (USCObject uscObject : objects)
		{
			if (!uscObject.getFieldValueToBoolen("ISENABLE"))
			{
				return true;
			}
		}
		return false;
	}

}
