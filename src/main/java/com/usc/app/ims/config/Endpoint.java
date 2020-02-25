package com.usc.app.ims.config;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.usc.app.ims.config.action.EndpointEnum;
import com.usc.app.wxdd.WxDdMessageService;
import com.usc.dto.Dto;
import com.usc.dto.impl.MapDto;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.api.impl.USCServerBeanProvider;
import com.usc.obj.util.USCObjectQueryHelper;

/**
 * @Author: lwp
 * @DATE: 2019/11/1 17:57
 * @Description:
 **/
@ServerEndpoint(value = "/ws/{userId}")
@Component
public class Endpoint
{
	@Autowired
	WxDdMessageService wxDdMessageService;
	// concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
	private static final String _msg_ = "_msg_";
	public static final Map<String, Session> users = new ConcurrentHashMap<>();

	/**
	 * webSocket连接
	 */
	@OnOpen
	public void onOpen(@PathParam("userId") String userId, Session session) throws IOException
	{
		// 把连接成功的userName当条件去查询用户id当键，创建session
		String condition = "DEL = 0 AND " + "ID = " + "'" + userId + "'";
		USCObject user = USCObjectQueryHelper.getObjectByID("SUSER", userId);
		ApplicationContext applicationContext = (ApplicationContext) USCServerBeanProvider.getContext("SUSER",
				user.getFieldNoByFieldName("SNAME"));
		// 设置最大空闲时间为一天
		session.setMaxIdleTimeout(1000 * 60 * 60 * 24);
		users.put(user.getID(), session);
		// 修改登录用户状态为在线
		try
		{
			applicationContext.setUserName((String) user.getFieldValue("SNAME"));
			applicationContext.setItemNo("SUSER");
			applicationContext.setCurrObj(user);
			user.setFieldValue("STATUS", "online");
			user.save(applicationContext);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		// 发送给所有在线用户，提示某某上线，前端刷新在线状态
		String allUserMessage = user.getFieldValue("SNAME") + Endpoint._msg_ + "" + Endpoint._msg_ + userId
				+ Endpoint._msg_ + "setOnline" + Endpoint._msg_ + "" + Endpoint._msg_ + new Date();
		this.sendToOnlineUser(user.getID(), allUserMessage);
		// 给自己发送一条消息前端获取后查询自己所有的通知
		this.sendToUser(user.getID(), "@NT_RUR");
		System.err.println("连接成功:" + "当前连接数：" + users.size());
	}

	/**
	 * 接收信息
	 *
	 * @param message 信息
	 * @param session
	 */
	@OnMessage
	public void onMessage(String message, Session session) throws Exception
	{

		// 发送消息时是带_msg_的,获取离线消息时带lx的
		if (message.contains(Endpoint._msg_))
		{
			String[] arr = message.split(Endpoint._msg_);
			String senderAvatar = arr[0]; // 发送者头像
			String senderId = arr[1]; // 发送者Id,群id
			String senderName = arr[2]; // 发送者姓名
			String receiverId = arr[3]; // 接收者Id
			String receiverName = arr[4]; // 接收者姓名
			String msg = arr[5]; // 消息
			String type = arr[6]; // 消息类型
			String datatime = String.valueOf(new Date()); // 时间
			String messages = senderName + "_msg_" + senderAvatar + "_msg_" + senderId + "_msg_" + type + "_msg_" + msg
					+ "_msg_" + datatime; // 私聊
			String groupMessages = senderName + "_msg_" + senderAvatar + "_msg_" + receiverId + "_msg_" + type + "_msg_"
					+ msg + "_msg_" + datatime + "_msg_" + senderId; // 群聊
			if ("group".equals(type))
			{
				String groupUserCondition = "DEL = 0 AND " + "GROUPID = " + "'" + receiverId + "'";
				USCObject[] groupUserList = USCObjectQueryHelper.getObjectsByCondition("CHAT_GROUP_USER",
						groupUserCondition);
				if (groupUserList != null)
				{
					for (int i = 0; i < groupUserList.length; i++)
					{
						Map<String, Object> gChat = new HashMap<>();
						gChat.put("SENDERID", groupUserList[i].getFieldValue("USERID"));
						gChat.put("SENDER_NAME", senderName);
//                    String senderAvatarId = senderAvatar.substring(senderAvatar.lastIndexOf("/") + 1);
//                    gChat.put("avatar", senderAvatarId);
						gChat.put("AVATAR", senderAvatar);
						gChat.put("RECEIVERID", receiverId);
						gChat.put("MSG", msg);
						gChat.put("CTIME", new Date());
						gChat.put("TYPES", type);
						if (senderId.equals(groupUserList[i].getFieldValue("USERID")))
						{
							gChat.put("STATUS", 1); // 不给自己发送群消息，设置已读，存为记录
						} else
						{
							boolean isSuccess = this.sendToUser((String) groupUserList[i].getFieldValue("USERID"),
									groupMessages);
							if (isSuccess)
							{
								gChat.put("STATUS", 1); // 设置已读
							} else
							{
								gChat.put("STATUS", 0); // 设置未读
							}
						}
						try
						{
							// 存聊天纪录
							ApplicationContext applicationContext = (ApplicationContext) USCServerBeanProvider
									.getContext("CHAT_HISTORY", senderName);
							applicationContext.setUserName(senderName);
							applicationContext.setItemNo("CHAT_HISTORY");
							applicationContext.setFormData(gChat);
							applicationContext.createObj("CHAT_HISTORY");
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			} else
			{
				Dto pChat = new MapDto();
				pChat.put("SENDERID", senderId);
				pChat.put("SENDER_NAME", senderName);
				pChat.put("AVATAR", senderAvatar);
				pChat.put("RECEIVERID", receiverId);
				pChat.put("RECEIVER_NAME", receiverName);
				pChat.put("MSG", msg);
				pChat.put("CTIME", new Date());
				pChat.put("TYPES", type);
				boolean isSuccess = this.sendToUser(receiverId, messages);
				if (isSuccess)
				{
					pChat.put("STATUS", 1); // 设置已读
				} else
				{
					for (Map.Entry<String, Session> user : users.entrySet())
					{
						if (user.getKey().equals(senderId) && user.getValue().isOpen())
						{
							try
							{
								String sysMessage = "系统通知" + "_msg_" + senderAvatar + "_msg_" + receiverId + "_msg_"
										+ type + "_msg_" + "对方现在离线，上线后接收到您的消息!" + "_msg_" + "NAN";
								user.getValue().getBasicRemote().sendText(sysMessage);
								pChat.put("STATUS", 0); // 设置未读
							} catch (IOException e)
							{
								e.printStackTrace();
							}
						} else if (!user.getValue().isOpen())
						{
							String sysMessage = "系统通知" + "_msg_" + senderAvatar + "_msg_" + receiverId + "_msg_" + type
									+ "_msg_" + "您已经掉线，请刷新界面或者再次登陆系统!" + "_msg_" + "NAN";
							user.getValue().getBasicRemote().sendText(sysMessage);
						}
					}
				}
				try
				{
					ApplicationContext applicationContext = (ApplicationContext) USCServerBeanProvider
							.getContext("SUSER", senderName);
					// 保存聊天纪录
					applicationContext.setUserName(senderName);
					applicationContext.setItemNo("CHAT_HISTORY");
					applicationContext.setFormData(pChat);
					applicationContext.createObj("CHAT_HISTORY");
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		} else if (message.contains("lx"))
		{
			// 获取离线聊天纪录
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String userId = message.substring(2);
//            String userCondition = "del = 0 AND " + "id = " + "'" + userId + "'";
//            USCObject[] userList = USCObjectQueryHelper.getObjectsByCondition("SUSER", userCondition);
//            String condition = "del = 0 AND " + "id = " + "'" + userId + "'";
			USCObject user = USCObjectQueryHelper.getObjectByID("SUSER", userId);
			ApplicationContext applicationContext = (ApplicationContext) USCServerBeanProvider.getContext("SUSER",
					user.getFieldNoByFieldName("SNAME"));
			String receiverId = user.getID();

			applicationContext.setUserName((String) user.getFieldValue("SNAME"));
			applicationContext.setItemNo("CHAT_HISTORY");

			String groupUserCondition = "DEL = 0 AND " + "SENDERID = " + "'" + userId + "'" + " AND STATUS = " + "'" + 0
					+ "'";
			USCObject[] gMessagesList = USCObjectQueryHelper.getObjectsByCondition("CHAT_HISTORY", groupUserCondition);
			if (gMessagesList != null)
			{
				for (int i = 0; i < gMessagesList.length; i++)
				{
					if ("group".equals(gMessagesList[i].getFieldValue("TYPES")))
					{
						Date date = df.parse(df.format(gMessagesList[i].getFieldValue("CTIME")));
						String gmsg = gMessagesList[i].getFieldValue("SENDER_NAME") + Endpoint._msg_
								+ gMessagesList[i].getFieldValue("AVATAR") + Endpoint._msg_
								+ gMessagesList[i].getFieldValue("RECEIVERID") + Endpoint._msg_
								+ gMessagesList[i].getFieldValue("TYPES") + Endpoint._msg_
								+ gMessagesList[i].getFieldValue("MSG") + Endpoint._msg_ + date.getTime()
								+ Endpoint._msg_ + gMessagesList[i].getFieldValue("SENDERID");
						boolean isSuccess = this.sendToUser(userId, gmsg);
						if (isSuccess)
						{
							// 修改数据状态
							gMessagesList[i].setFieldValue("STATUS", 1);
							try
							{
								gMessagesList[i].save(applicationContext);
							} catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					}
				}
			}
			String condition = "DEL = 0 AND " + "RECEIVERID = " + "'" + receiverId + "'" + " AND " + "STATUS = " + 0;
			USCObject[] pMessagesList = USCObjectQueryHelper.getObjectsByCondition("CHAT_HISTORY", condition);
			if (pMessagesList != null)
			{
				for (USCObject object : pMessagesList)
				{
					Date date = df.parse(df.format(object.getFieldValue("CTIME")));
					String pmsg = object.getFieldValue("SENDER_NAME") + Endpoint._msg_ + object.getFieldValue("AVATAR")
							+ Endpoint._msg_ + object.getFieldValue("SENDERID") + Endpoint._msg_
							+ object.getFieldValue("TYPES") + Endpoint._msg_ + object.getFieldValue("MSG")
							+ Endpoint._msg_ + date.getTime();
					boolean isSuccess = this.sendToUser(receiverId, pmsg);
					if (isSuccess)
					{
						// 修改数据状态
						applicationContext.setCurrObj(object);
						object.setFieldValue("STATUS", 1);
						try
						{
							object.save(applicationContext);
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	// 处理错误
	@OnError
	public void onError(Throwable error, Session session)
	{
		for (Map.Entry<String, Session> i : users.entrySet())
		{
			if (session.equals(i.getValue()) && session.isOpen())
			{
				String condition = "DEL = 0 AND " + "ID = " + "'" + i.getKey() + "'";
//                USCObject userList = USCObjectQueryHelper.getObjectsByCondition("SUSER", condition);
				USCObject user = USCObjectQueryHelper.getObjectByID("SUSER", i.getKey());
				ApplicationContext applicationContext = (ApplicationContext) USCServerBeanProvider
						.getContext(user.getFieldValueToString("SNAME"), user);
				// 修改登录用户状态为在线
//                applicationContext.setUserName((String) user.getFieldValue("SNAME"));
//                applicationContext.setItemNo("SUSER");
//                applicationContext.setCurrObj(user);
				user.setFieldValue("STATUS", "offline");
				try
				{
					user.save(applicationContext);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				// 修改数据后移除
				users.remove(i.getKey());
			}
		}
		System.err.println("webSocket发生错误了!:" + error + "当前连接数：" + users.size());
	}

	// 处理连接关闭
	@OnClose
	public void onClose(Session session) throws IOException
	{
		for (Map.Entry<String, Session> i : users.entrySet())
		{
			if (session.equals(i.getValue()) && session.isOpen())
			{
				USCObject user = USCObjectQueryHelper.getObjectByID("SUSER", i.getKey());
				try
				{
					ApplicationContext applicationContext = (ApplicationContext) USCServerBeanProvider
							.getContext(user.getFieldValueToString("SNAME"), user);
					// 修改登录用户状态为在线
					applicationContext.setUserName((String) user.getFieldValue("SNAME"));
					applicationContext.setItemNo("SUSER");
					applicationContext.setCurrObj(user);
					user.setFieldValue("STATUS", "offline");
					user.save(applicationContext);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				// 发送给所有在线用户，不提示某某下线，前端刷新在线状态为离线头像置灰
				String allUserMessage = user.getFieldValue("SNAME") + Endpoint._msg_ + "" + Endpoint._msg_
						+ user.getID() + Endpoint._msg_ + "setOffline" + Endpoint._msg_ + "" + Endpoint._msg_
						+ new Date();
				this.sendToOnlineUser(user.getID(), allUserMessage);
				// 修改数据后移除
				users.remove(i.getKey());
			}
		}
		System.err.println("连接关闭:" + "当前连接数：" + users.size());
	}

	// webSocket消息发送
	public boolean sendToUser(String receiverId, String message) throws IOException
	{
		boolean result = false;
		for (Map.Entry<String, Session> user : users.entrySet())
		{
			if (user.getKey().equals(receiverId) && user.getValue().isOpen())
			{
				user.getValue().getBasicRemote().sendText(message);
				result = true;
			} else if (!user.getValue().isOpen())
			{
				String sysMessage = "系统通知" + "_msg_" + null + "_msg_" + receiverId + "_msg_" + "friend" + "_msg_"
						+ "您已经掉线，请刷新界面或者再次登陆系统!" + "_msg_" + "NAN";
				user.getValue().getBasicRemote().sendText(sysMessage);
			}
		}
		return result;
	}

	// webSocket消息发送
	public boolean sendToUser(String receiverId, String message, EndpointEnum endpointEnum) throws IOException
	{
		if (endpointEnum == null)
		{
			return sendToUser(receiverId, message);
		} else
		{
			return sendToUser(receiverId, endpointEnum.code);
		}
	}

	// webSocket消息发送给在线用户除开自己
	public void sendToOnlineUser(String userId, String message) throws IOException
	{
		for (Map.Entry<String, Session> user : users.entrySet())
		{
			if (user.getValue().isOpen() && !userId.equals(user.getKey()))
			{
				user.getValue().getBasicRemote().sendText(message);
			}
		}
	}

	// rabbitmq消息通过webSocket发送给在线用户
	public void sendMessageOnline(String message) throws Exception
	{
		System.err.println("websocket中:" + message);
		for (Map.Entry<String, Session> user : users.entrySet())
		{
			user.getValue().getBasicRemote().sendText(message);
		}
	}
}
