package com.usc.interceptor;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.usc.app.sys.online.OnlineUsers;
import com.usc.app.util.servlet.ServerletUtils;
import com.usc.interceptor.a.APPHandlerInterceptor;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OnlineInterceptor extends APPHandlerInterceptor {
	private NamedThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal<Long>("StopWatch-StartTime");

	@Override
	public boolean beforeHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		JSONObject jsonObject = new JSONObject();
		String userName = request.getHeader("UserName");
		if (userName == null || "undefined".equals(userName))
		{
			jsonObject.put("flag", false);
			jsonObject.put("info", messageSourceUtil.getMessage("User_Exist_0", request.getLocale()));
			ServerletUtils.returnResponseJson(response, jsonObject.toJSONString());
			return false;
		}
		Locale locale = OnlineUsers.getOnUser(userName).getLocale();
		String clientID = request.getHeader("clientID");
		if (clientID == null || "undefined".equals(clientID))
		{
			jsonObject.put("flag", false);
			jsonObject.put("info", messageSourceUtil.getMessage("UnauthorizedOperation_INFO", locale) + " ["
					+ messageSourceUtil.getMessage("UnauthorizedOperation_CODE") + "]");
			ServerletUtils.returnResponseJson(response, jsonObject.toJSONString());
			return false;
		} else
		{
			if (OnlineUsers.getOnClientUser(clientID) == null)
			{
				jsonObject.put("flag", false);
				jsonObject.put("path", messageSourceUtil.getMessage("OfflineLoginAgain_PATH", locale));
				jsonObject.put("info", messageSourceUtil.getMessage("OfflineLoginAgain_INFO", locale) + " ["
						+ messageSourceUtil.getMessage("OfflineLoginAgain_CODE", locale) + "]");
				ServerletUtils.returnResponseJson(response, jsonObject.toJSONString());
				return false;

			}
		}
		long beginTime = System.currentTimeMillis();
		startTimeThreadLocal.set(beginTime);
		return true;
	}

	@Override
	public void afterHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		long endTime = System.currentTimeMillis();
		long beginTime = startTimeThreadLocal.get();
		long consumeTime = endTime - beginTime;
		if (consumeTime > 500)
		{ log.warn(String.format("%s consume %d millis", request.getRequestURI(), consumeTime)); }
	}

	@Override
	public void afterReaderCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) throws Exception {
//		System.out.println("》》》》》》》》》》》》》》》》》》》页面渲染完成");
	}
}
