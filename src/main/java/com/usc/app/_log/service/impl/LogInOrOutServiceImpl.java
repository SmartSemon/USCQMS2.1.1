package com.usc.app._log.service.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.usc.app._log.service.LogInOrOutService;
import com.usc.app.sys.online.OnlineUsers;
import com.usc.app.util.JwtUtils;
import com.usc.app.util.UserInfoUtils;
import com.usc.app.util.tran.FormatPromptInformation;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.conf.cf.i18n.MessageSourceUtil;
import com.usc.obj.api.bean.UserInformation;
import com.usc.obj.api.impl.USCServerBeanProvider;
import com.usc.server.syslog.LOGActionEnum;

@Service("logInOrOutService")
public class LogInOrOutServiceImpl implements LogInOrOutService {
	@Autowired
	private MessageSourceUtil messageSourceUtil;

	@Override
	public Map<String, Object> Login(Map<String, Object> parameter) {
		String userName = (String) parameter.get("userName");
		HttpServletRequest request = (HttpServletRequest) parameter.get("request");
		HttpServletResponse response = (HttpServletResponse) parameter.get("response");
		return clientInfo(userName, request, response);

	}

	private Map<String, Object> clientInfo(String userName, HttpServletRequest request, HttpServletResponse response) {
		String ClientID = null;
		String token = null;
		Map<String, Object> result = StandardResultTranslate
				.getResult(FormatPromptInformation.getLoginMsg("Login_Success"));
		UserInformation userInformation = UserInfoUtils.getUserInformation(userName);
		Locale locale = request.getLocale();
		if (UserInfoUtils.isSuperAdministrator(userName))
		{
			ClientID = userName;
			userInformation.setClientID(ClientID);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("UserName", userName);
			token = JwtUtils.buildJwt(map);
		} else
		{
			if (userInformation == null)
			{
				result.put("flag", false);
				result.put("info", messageSourceUtil.getMessage("UserInfo_Exist_0", locale));
				return result;
			}
			ClientID = UserInfoUtils.getUserWKContextID(userInformation);
			userInformation.setClientID(ClientID);
			token = JwtUtils.buildJwt(UserInfoUtils.createUserInfoMap(userName));

		}
		if (userInformation != null)
		{
			result.put("employeeName", userInformation.getEmployeeName());
			result.put("userName", userInformation.getUserName());
			result.put("userId", userInformation.getUserID());
			UserInfoUtils.putUserInfomation(userInformation);
		}
		if (ClientID != null)
		{ result.put("clientID", ClientID); }
		result.put("token", "Bearer " + token);
		OnlineUsers.addOnlineUser(request, userInformation);
		result.put("dataList", Navigation.getAuthorized(userName));
		result.put("AcceptLanguage", OnlineUsers.getOnUser(userName).getLocale().toLanguageTag());
		USCServerBeanProvider.getSystemLogger().writeLOGINLog(userInformation, LOGActionEnum.LOGIN);
		return result;
	}

	@Override
	public Map<String, Object> Logout(Map<String, Object> parameter) {
		String userName = (String) parameter.get("userName");
		Locale locale = OnlineUsers.getOnUser(userName).getLocale();
		if (userName != null && OnlineUsers.isOnline(userName))
		{
			USCServerBeanProvider.getSystemLogger().writeLOGOUTLog(UserInfoUtils.getUserInformation(userName),
					LOGActionEnum.LOGOUT);
			OnlineUsers.removeUser(userName);
		}
		return StandardResultTranslate.getResult(messageSourceUtil.getMessage("Logout_Success", locale), true);
	}

}
