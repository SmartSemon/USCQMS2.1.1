package com.usc.app.util;

import java.io.IOException;
import java.util.HashMap;

import com.usc.app.ims.config.Endpoint;
import com.usc.app.ims.config.action.EndpointEnum;
import com.usc.cache.redis.RedisUtil;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.bean.UserInformation;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.api.impl.USCServerBeanProvider;
import com.usc.util.ObjectHelperUtils;
import com.usc.util.SpringContextUtil;

public class SendMessageUtils
{
	private static Endpoint endpoint = null;

	private static Endpoint getEndpoint()
	{
		return (Endpoint) SpringContextUtil.getBean(Endpoint.class);
	}

	public static boolean sendNewsToUser(EndpointEnum endpointEnum, ApplicationContext context, String title,
			String message, String sendUserName, String... toUserName)
	{
		if (title == null || toUserName == null)
		{
			return false;
		}

		return sendMessage(endpointEnum, context, "news", title, message, sendUserName, toUserName);
	}

	public static boolean sendNewsToUser(EndpointEnum endpointEnum, ApplicationContext context, USCObject[] objects,
			String title, String message, String sendUserName, String... toUserName)
	{
		if (title == null || toUserName == null)
		{
			return false;
		}
		if (ObjectHelperUtils.isEmpty(objects))
		{
			return sendMessage(endpointEnum, context, "news", title, message, sendUserName, toUserName);
		} else
		{
			return sendMessage(endpointEnum, context, objects, "news", title, message, sendUserName, toUserName);
		}

	}

	public static boolean sendNoticeToUser(EndpointEnum endpointEnum, ApplicationContext context, String title,
			String message, String sendUserName, String... toUserName)
	{
		if (title == null || toUserName == null)
		{
			return false;
		}

		return sendMessage(endpointEnum, context, "notice", title, message, sendUserName, toUserName);
	}

	public static boolean sendTodoToUser(EndpointEnum endpointEnum, ApplicationContext context, String title,
			String message, String sendUserName, String... toUserName)
	{
		if (title == null || toUserName == null)
		{
			return false;
		}

		return sendMessage(endpointEnum, context, "todo", title, message, sendUserName, toUserName);
	}

	/**
	 * @param endpointEnum 标记枚举 allow null
	 * @param context      上下文环境 allow null
	 * @param type         消息类型 news、notice、todo
	 * @param title        消息标题
	 * @param message      消息内容
	 * @param sendUserName 发送人
	 * @param toUserName   接收人数组
	 * @return
	 */
	public static boolean sendToUser(EndpointEnum endpointEnum, ApplicationContext context, USCObject[] objects,
			String type, String title, String message, String sendUserName, String... toUserName)
	{

		return sendMessage(endpointEnum, context, objects, type, title, message, sendUserName, toUserName);
	}

	private static boolean sendMessage(EndpointEnum endpointEnum, ApplicationContext context, String type, String title,
			String message, String sendUserName, String... toUserName)
	{
		RedisUtil redis = RedisUtil.getInstanceOfObject();

		ApplicationContext context1 = instContext(context);
		for (String userName : toUserName)
		{
			String toUserID = getUserID(redis, userName);
			try
			{
				createMessageObj(context1, type, title, message, sendUserName, toUserID, userName);
				send(endpointEnum, toUserID, title);
			} catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	private static void send(EndpointEnum endpointEnum, String toUserID, String title) throws IOException
	{
		endpoint = getEndpoint();
		if (endpointEnum == null)
		{
			endpoint.sendToUser(toUserID, title);
		} else
		{
			switch (endpointEnum)
			{
			case RefreshUnread:
				endpoint.sendToUser(toUserID, EndpointEnum.RefreshUnread.code);
				break;
			case RefreshRead:
				endpoint.sendToUser(toUserID, EndpointEnum.RefreshRead.code);
				break;

			default:

				break;
			}
		}
	}

	private static boolean sendMessage(EndpointEnum endpointEnum, ApplicationContext context, USCObject[] objects,
			String type, String title, String message, String sendUserName, String... toUserName)
	{
		RedisUtil redis = RedisUtil.getInstanceOfObject();
		ApplicationContext context1 = instContext(context);
		for (String userName : toUserName)
		{
			String toUserID = getUserID(redis, userName);
			try
			{
				USCObject mesObj = createMessageObj(context1, type, title, message, sendUserName, toUserID, userName);
				if (ObjectHelperUtils.isNotEmpty(objects))
				{
					createMessageRelationObjs(context1, mesObj, objects);
				}
				send(endpointEnum, toUserID, title);
			} catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	private static USCObject createMessageObj(ApplicationContext context, String type, String title, String message,
			String sendUserName, String toUserID, String toUserName) throws Exception
	{
		HashMap<String, Object> msinfo = new HashMap<String, Object>();
		msinfo.put("TITLE", title);
		if (message != null && message.startsWith("<p>") && message.endsWith("</p>"))
		{
			message = message.replace("<p>", "").replace("</p>", "");
		}
		msinfo.put("SMESSAGE", message);
		msinfo.put("SENDERID", sendUserName);
		msinfo.put("USERID", toUserID);
		msinfo.put("USERNAME", toUserName);
		msinfo.put("STATUS", 0);
		msinfo.put("TYPE", type);
		context.setFormData(msinfo);
		return context.createObj("NOTICE");

	}

	private static void createMessageRelationObjs(ApplicationContext context, USCObject mesObj, USCObject... objects)
			throws Exception
	{
		HashMap<String, Object> msinfo = new HashMap<String, Object>();
		msinfo.put("ITEMNO", mesObj.getItemNo());
		msinfo.put("FK_NOTICE_ID", mesObj.getID());
		ApplicationContext context1 = (ApplicationContext) USCServerBeanProvider.getContext("NOTICE_ITEMNO",
				context.getUserName());
		for (USCObject object : objects)
		{
			msinfo.put("ITEMNO_ID", object.getID());
			context1.setFormData(msinfo);
			context1.createObj("NOTICE_ITEMNO");
		}
	}

	private static ApplicationContext instContext(ApplicationContext context)
	{
		String cuser = null;
		if (context != null)
		{
			cuser = context.getUserName();
		} else
		{
			cuser = "admim";
		}
		return (ApplicationContext) USCServerBeanProvider.getContext("NOTICE", cuser);
	}

	private static String getUserID(RedisUtil redis, String userName)
	{
		if (userName != null)
		{
			UserInformation userInformation = UserInfoUtils.getUserInformation(userName);
			return userInformation.getUserID();
		}
		return null;
	}

}
