package com.usc.app.wxdd.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageParamesConfig
{
	// 1.微信参数
	// token
	public final static String token = "ray";
	// encodingAESKey
	public final static String encodingAESKey = "z2W9lyOAR1XjY8mopEmiSqib0TlBZzCFiCLp6IdS2Iv";

	// 通讯录秘钥
	public final static String contactsSecret = "1m_9XP62YrXjSxxxxxiLVWBThukiK5sH7wm1TM";
	// 打卡的凭证密钥
	public final static String checkInSecret = "LLTMcHo5oxxxxxU0F6wX_gRIc";
	// 审批的凭证密钥
	public final static String approveSecret = "6X7Ft0hIZXYxxxxxefWZE0-8";

	// 企业微信获取授权凭证URL
	public final static String weixin_access_token_url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid={{corpId}}&corpsecret={{corpsecret}}";
	// 发送企业微信URL
	public final static String sendWeiXinMessage_url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";
	// 上传媒体文件到企业微信微信服务器URL
	public final static String uploadMediaMaterial_url = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";

	@Value("${qywx.corpId}")
	public String corpId;
	@Value("${qywx.agentSecret}")
	public String agentSecret;
	@Value("${qywx.agentId}")
	public int agentId;;
}
