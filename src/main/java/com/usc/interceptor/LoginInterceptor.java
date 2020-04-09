package com.usc.interceptor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.usc.app._log.service.LogInOrOutService;
import com.usc.app._log.service.impl.Navigation;
import com.usc.app.sys.online.OnlineUsers;
import com.usc.app.util.UserInfoUtils;
import com.usc.app.util.servlet.ServerletUtils;
import com.usc.app.util.tran.FormatPromptInformation;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.autho.MD5.MD5Util;
import com.usc.interceptor.a.APPHandlerInterceptor;
import com.usc.obj.api.impl.USCServerBeanProvider;
import com.usc.server.syslog.LOGActionEnum;
import com.usc.server.util.BeanFactoryConverter;
import com.usc.test.mate.jsonbean.USCObjectJSONBean;
import com.usc.test.mate.resource.ServiceToWbeClientResource;
import com.usc.util.ObjectHelperUtils;

@Component
public class LoginInterceptor extends APPHandlerInterceptor {
	@Autowired
	private LogInOrOutService logInOrOutService;
	private String userName = null;

	@Override
	public boolean beforeHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		ServletRequest requestWapper = ServerletUtils.getServletRequestWapper(request);
		if (requestWapper != null)
		{
			String json = ServerletUtils.getRequestJsonParam((HttpServletRequest) requestWapper);
			JSONObject jsonObject = JSONObject.parseObject(json);

			String userName = (String) jsonObject.get("userName");
			String password = String.valueOf(jsonObject.get("userPassword"));
			boolean flag = UserInfoUtils.userExistence(userName);
			if (!flag)
			{
				ServerletUtils
						.returnResponseJson(response,
								new JSONObject(StandardResultTranslate
										.getResult(FormatPromptInformation.getLoginMsg("User_Exist_0"), false))
												.toJSONString());
			} else
			{
				try
				{
					if (!MD5Util.validMD5Legitimacy(password, UserInfoUtils.getPassWord(userName)))
					{
						ServerletUtils.returnResponseJson(response,
								new JSONObject(StandardResultTranslate
										.getResult(FormatPromptInformation.getLoginMsg("Incorrect_Password"), false))
												.toJSONString());
						return false;
					}
					this.userName = userName;
					jsonObject.put("response", response);
					jsonObject.put("request", request);
					if (jsonObject.containsKey("force"))
					{
						boolean force = jsonObject.getBooleanValue("force");
						if (force)
						{
							ServiceToWbeClientResource.webPageModelData.remove(userName);
							ServerletUtils.returnResponseJson(response,
									new JSONObject(logInOrOutService.Login(jsonObject)).toJSONString());
							return true;

						} else
						{
							ServerletUtils.returnResponseJson(response,
									new JSONObject(StandardResultTranslate
											.getResult(FormatPromptInformation.getLoginMsg("Login_Fail"), false))
													.toJSONString());
						}

					} else
					{

						if (OnlineUsers.isOnline(userName))
						{
							Map<String, Object> map = StandardResultTranslate
									.getResult(FormatPromptInformation.getLoginMsg("Login_Force"), true);
							map.put("force", true);
							ServerletUtils.returnResponseJson(response, new JSONObject(map).toJSONString());
						} else
						{
							ServerletUtils.returnResponseJson(response,
									new JSONObject(logInOrOutService.Login(jsonObject)).toJSONString());
							return true;
						}
					}
				} catch (Exception e)
				{
					if (OnlineUsers.isOnline(userName))
					{
						USCServerBeanProvider.getSystemLogger()
								.writeLOGOUTLog(UserInfoUtils.getUserInformation(userName), LOGActionEnum.LOGOUT);
						OnlineUsers.removeUser(userName);
					}
					e.printStackTrace();
					ServerletUtils.returnResponseJson(response,
							new JSONObject(StandardResultTranslate
									.getResult(FormatPromptInformation.getLoginMsg("Login_Exception"), false))
											.toJSONString());
				}

			}

		}

		return false;
	}

	@Override
	public void afterHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (userName != null)
		{
			Long st = System.currentTimeMillis();

			List<Map<String, Object>> navigations = Navigation.getAuthorized(userName);
			if (ObjectHelperUtils.isNotEmpty(navigations))
			{
				Map<String, Map<String, Object>> userModel = new ConcurrentHashMap<String, Map<String, Object>>();
				ServiceToWbeClientResource resource = new ServiceToWbeClientResource();

				for (Map<String, Object> navigation : navigations)
				{
					Integer faceType = (Integer) navigation.get("FACETYPE");
					if (faceType != null)
					{
						if (0 != faceType)
						{
							String pageID = (String) navigation.get("ID");
							String modelParams = (String) navigation.get("PARAMS");
							if (ObjectHelperUtils.isNotEmpty(modelParams))
							{
								JSONObject paramObject = JSONObject.parseObject(modelParams);
								paramObject.put("userName", userName);
								paramObject.put("pageId", pageID);
								paramObject.put("facetype", faceType);
								Map<String, Object> model = null;
								String reqParam = paramObject.toJSONString();
								if (faceType == 4)
								{
									model = resource.getClassNodeModelData(reqParam, request);
								} else if (faceType == 5)
								{
									model = resource.getClassViewModelData(reqParam, request);

								} else
								{
									model = resource.getModelData(
											BeanFactoryConverter.getJsonBean(USCObjectJSONBean.class, reqParam),
											request);
								}
								if ((boolean) model.get("flag"))
								{ userModel.put(pageID, model); }

							}
						}
					}
				}
				String userModelKey = OnlineUsers.getUserNameAcceptLanguage(userName);
				ServiceToWbeClientResource.webPageModelData.put(userModelKey, userModel);
				System.out.println(
						"<<<<<<<<<< 用户 [ " + userName + " ] 建模加载耗时：           " + (System.currentTimeMillis() - st));
			}
		}
	}

	@Override
	protected void afterReaderCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) throws Exception {
		System.out.println("获取国际化登陆语言：" + messageSourceUtil.getMessage("Login_Success", request.getLocale()));
		System.out.println("页面渲染完成");

	}

}
