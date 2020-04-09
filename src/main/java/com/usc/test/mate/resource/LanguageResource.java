package com.usc.test.mate.resource;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.usc.app._log.service.impl.Navigation;
import com.usc.app.sys.online.OnlineUsers;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.server.util.BeanFactoryConverter;
import com.usc.test.mate.jsonbean.USCObjectJSONBean;
import com.usc.util.ObjectHelperUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/language", produces = "application/json;charset=UTF-8")
@Api(value = "/language", tags = "{TEST}")
public class LanguageResource {
	@PostMapping("/switch")
	@ApiOperation(value = "/switch", httpMethod = "POST", notes = "多语言切换")
	public Object getNavigation(@RequestBody String queryParam, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String userName = request.getHeader("UserName");
		String[] lag = queryParam.replace("\"", "").split("-");
		OnlineUsers.setOnlieUserLocale(userName, new Locale(lag[0], lag[1]));
		List<Map<String, Object>> navigations = Navigation.getAuthorized(userName);
		String userModelKey = OnlineUsers.getUserNameAcceptLanguage(userName);
		if (ObjectHelperUtils.isNotEmpty(navigations)
				&& !ServiceToWbeClientResource.webPageModelData.containsKey(userModelKey))
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
										BeanFactoryConverter.getJsonBean(USCObjectJSONBean.class, reqParam), request);
							}
							if ((boolean) model.get("flag"))
							{ userModel.put(pageID, model); }

						}
					}
				}
			}

			ServiceToWbeClientResource.webPageModelData.put(userModelKey, userModel);
		}

		Map<String, Object> result = StandardResultTranslate.getResult("Language switch success", navigations);

		return result;
	}
}
