package com.usc.app.action.demo.zc;

import java.util.Date;
import java.util.HashMap;

import javax.validation.constraints.NotNull;

import com.usc.app.ims.config.action.EndpointEnum;
import com.usc.app.util.SendMessageUtils;
import com.usc.app.wxdd.WxDdMessageService;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.api.impl.USCServerBeanProvider;
import com.usc.util.SpringContextUtil;

/**
 * @author Semon
 * @implNote	创建预警信息
 */
public class CreateEarlyWarningInfo {
	/**
	 * @param username		创建人
	 * @param dwuser		处理人
	 * @param info			预警内容
	 * @param type			预警类别
	 * @param severity		严重程度
	 * @param influences	影响范围
	 * @param continuedtime	预计持续时间
	 * @param measures		应对措施
	 */
	public static void create(String userName,@NotNull String dwuser,@NotNull String info,
			@NotNull String type,@NotNull String severity,@NotNull String cycle,
			String influences,String continuedtime,String measures) 
	{
		HashMap<String, Object> hashMap = new HashMap<String, Object>(8);
		hashMap.put("DWUSER", dwuser);
		hashMap.put("NAME", info);
		hashMap.put("TYPE", type);
		hashMap.put("SEVERITY", severity);
		hashMap.put("CYCLE", cycle);
		hashMap.put("INFLUENCES", influences);
		hashMap.put("CONTINUEDTIME", continuedtime);
		hashMap.put("MEASURES", measures);
		hashMap.put("NTIME", new Date());
		ApplicationContext context = (ApplicationContext) USCServerBeanProvider.getContext("EARLYWARNING", userName);
		context.setFormData(hashMap);
		try 
		{
			USCObject object = context.createObj("EARLYWARNING");
			WxDdMessageService wxddService = SpringContextUtil.getBean(WxDdMessageService.class);
			wxddService.sendTextMessage("质量系统预警提醒:\r\n\t内容："+info+"\r\n\t严重程度："+object.getFieldValueToString("SEVERITY"), dwuser, null, null);
			SendMessageUtils.sendToUser(EndpointEnum.RefreshUnread, context, null, "notice", "质量系统预警提醒", info, userName,
					dwuser.split(","));
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
