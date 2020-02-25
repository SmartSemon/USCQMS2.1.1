package com.usc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.usc.app._log.service.LogInOrOutService;
import com.usc.app.util.servlet.ServerletUtils;
import com.usc.autho.UserAuthority;
import com.usc.interceptor.a.APPHandlerInterceptor;
import com.usc.test.mate.resource.ServiceToWbeClientResource;

@Component
public class LogoutInterceptor extends APPHandlerInterceptor
{
	@Autowired
	private LogInOrOutService logInOrOutService;

	private String userName;

	@Override
	public boolean beforeHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception
	{
		String json = ServerletUtils.getRequestJsonParam(request);
		JSONObject jsonObject = JSONObject.parseObject(json);
		userName = jsonObject.getString("userName");
		ServerletUtils.returnResponseJson(response,
				new JSONObject(logInOrOutService.Logout(jsonObject)).toJSONString());

		return true;
	}

	@Override
	public void afterHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception
	{
		ServiceToWbeClientResource.webPageModelData.remove(userName);
		UserAuthority.removeUserRightMenus(userName);
	}

	@Override
	protected void afterReaderCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) throws Exception
	{

	}

}
