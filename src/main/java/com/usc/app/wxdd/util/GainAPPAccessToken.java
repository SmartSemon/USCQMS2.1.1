package com.usc.app.wxdd.util;

import javax.validation.constraints.NotNull;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.usc.app.util.http.HttpURLConnectionUtils;
import com.usc.app.wxdd.AccessToken;
import com.usc.cache.redis.RedisUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 获取微信或钉钉服务器授权凭证
 * </p>
 *
 * @author SEMON
 *
 */
@Slf4j
public class GainAPPAccessToken
{

	static RedisUtil redisUtil = RedisUtil.getInstanceOfObject();

	public static AccessToken getAccessToken(@NotNull String appid, @NotNull String appsecret)
	{
		AccessToken accessToken = (AccessToken) redisUtil.get("WEIXIN_ACCESSTOKEN");
		if (accessToken == null)
		{
			String requestUrl = MessageParamesConfig.weixin_access_token_url.replace("{{corpId}}", appid)
					.replace("{{corpsecret}}", appsecret);
			JSONObject jsonObject = HttpURLConnectionUtils.sendGetHttpRequest(requestUrl);
			if (null != jsonObject)
			{
				try
				{
					accessToken = new AccessToken();
					String token = jsonObject.getString("access_token");
					accessToken.setToken(token);
					int expires_in = jsonObject.getIntValue("expires_in");
					accessToken.setExpiresIn(expires_in);
					redisUtil.set("WEIXIN_ACCESSTOKEN", accessToken, (expires_in - 60));
				} catch (JSONException e)
				{
					accessToken = null;
					log.error("获取token失败 errcode:{} errmsg:{}" + jsonObject.getIntValue("errcode") + ":"
							+ jsonObject.getString("errmsg"), e);
				}
			}
		}

		return accessToken;
	}
}
