package com.usc.app._log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usc.app._log.service.LogInOrOutService;

@RestController
@RequestMapping(produces = "application/json;charset=UTF-8")
public class LogInOrOut
{
	@Autowired
	private LogInOrOutService logInOrOutService;

	@PostMapping(value = "/login")
	public Object login(@RequestBody(required = false) String param, HttpServletRequest request,
			HttpServletResponse response)
	{
//		Assert.notNull(param);
//		Map<String, Object> parameter = JSONObject.parseObject(param);
//		parameter.put("response", response);
//		parameter.put("request", request);
//		if (!ObjectHelperUtils.isEmpty(param))
//		{
//			parameter.putAll(JSONObject.parseObject(param));
//		}
		return null;

	}

	@PostMapping(value = "/logout")
	public Object logout(@RequestBody(required = false) String param, HttpServletRequest request,
			HttpServletResponse response)
	{
//		Assert.notNull(param);
//		Map<String, Object> parameter = JSONObject.parseObject(param);
//		parameter.put("response", response);
//		parameter.put("request", request);
//		if (!ObjectHelperUtils.isEmpty(param))
//		{
//			parameter.putAll(JSONObject.parseObject(param));
//		}
		return null;

	}

}
