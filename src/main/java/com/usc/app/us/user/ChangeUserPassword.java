package com.usc.app.us.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.usc.app.util.UserInfoUtils;
import com.usc.app.util.tran.FormatPromptInformation;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.autho.MD5.MD5Util;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.ApplicationContext;

@RestController
@RequestMapping(produces = "application/json;charset=UTF-8")
public class ChangeUserPassword
{
	@PostMapping(value = "/modifiyUPS")
	public Object modifiy(@RequestBody(required = false) String param, HttpServletRequest request,
			HttpServletResponse response)
	{

		String clientID = request.getHeader("ClientID");
		if (clientID != null)
		{
			JSONObject jsonObject = JSONObject.parseObject(param);
			String oldPS = jsonObject.getString("password");
			String newPS = jsonObject.getString("newPassword");
			int length = Integer.valueOf(UserInfoUtils.getDefaultPassWordLength());
			if (newPS.length() > length)
			{
				return StandardResultTranslate.getResult(FormatPromptInformation.getUserMsg("PasswordOverlength")
						+ "	" + FormatPromptInformation.getUserMsg("PasswordMAXlength") + "[" + length + "]", false);
			}
			USCObject user = UserInfoUtils.getUserInfoByClientID(clientID);
			if (user != null)
			{
//				USCObject user = new UserInfoObject("SUSER", (HashMap<String, Object>) map);

				String userName = (String) user.getFieldValue("SNAME");
				ApplicationContext context = new ApplicationContext(userName, user);
				try
				{
					if (!MD5Util.validMD5Legitimacy(oldPS, UserInfoUtils.getPassWord(userName)))
					{
						return StandardResultTranslate
								.getResult(FormatPromptInformation.getUserMsg("InvalidHisPassword"), false);
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				context.setUserName(userName);
				context.setCurrObj(user);
				user.setFieldValue("PASSWORD", MD5Util.getMD5Ciphertext(newPS));
				user.save(context);
				return StandardResultTranslate.getResult(true, "Action_Update");
			}

		}
		return StandardResultTranslate.getResult(false, "Action_Update");

	}
}
