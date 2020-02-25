package com.usc.app.wxdd;

import java.io.File;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.usc.app.util.http.HttpURLConnectionUtils;
import com.usc.app.wxdd.msgbean.base.BaseMessage;
import com.usc.app.wxdd.msgbean.medai.FileMessage;
import com.usc.app.wxdd.msgbean.medai.ImageMessage;
import com.usc.app.wxdd.msgbean.medai.MessageMedia;
import com.usc.app.wxdd.msgbean.medai.VoiceMessage;
import com.usc.app.wxdd.msgbean.text.TextMessage;
import com.usc.app.wxdd.msgbean.text.TextMessageContent;
import com.usc.app.wxdd.util.AppMessageTypeEnum;
import com.usc.app.wxdd.util.GainAPPAccessToken;
import com.usc.app.wxdd.util.MessageParamesConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 发送微信或钉钉消息
 * </p>
 *
 * @author SEMON
 *
 *
 */
@Component
@Slf4j
public class WxDdMessageService
{

	@Autowired
	MessageParamesConfig messageParamesUtil;

	/**
	 * @param accessToken 外部应用服务器授权凭证
	 * @param message     消息体
	 */
	private JSONObject sendMessage(String accessToken, BaseMessage message)
	{
		if (accessToken == null || message == null)
		{
			log.error("parameter accessToken or message is null,send failed");
			return null;
		}

		message.setAgentid(messageParamesUtil.agentId);

		String jsonMessage = JSONObject.toJSONString(message);

		String sendWeiXinMessage_url = MessageParamesConfig.sendWeiXinMessage_url.replace("ACCESS_TOKEN", accessToken);

		JSONObject jsonObject = HttpURLConnectionUtils.sendPostRequest(sendWeiXinMessage_url, jsonMessage);
		if (null != jsonObject)
		{
			if (0 != jsonObject.getInteger("errcode"))
			{
				log.error("消息发送失败 errcode:{} errmsg:{}", jsonObject.getInteger("errcode"),
						jsonObject.getString("errmsg"));
			}
		}
		return jsonObject;
	}

	private JSONObject sendMessage(BaseMessage message)
	{
		String accessToken = gainAccessToken();
		return sendMessage(accessToken, message);
	}

	private String gainAccessToken()
	{
		String accessToken = GainAPPAccessToken
				.getAccessToken(messageParamesUtil.corpId, messageParamesUtil.agentSecret).getToken();
		return accessToken;
	}

	/**
	 * @param content 消息文本支持
	 *                <p>
	 *                <a href="http://work.weixin.qq.com">质量系统企业微信群</a>标签
	 *                </p>
	 * @param touser
	 *                <p>
	 *                成员ID列表（多个接收者用‘|’分隔，最多支持1000个）.指定为”@all”，则向该企业应用的全部成员发送
	 *                </p>
	 * @param toparty
	 *                <p>
	 *                部门ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为”@all”时忽略本参数
	 *                </p>
	 * @param totag
	 *                <p>
	 *                标签ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为”@all”时忽略本参数
	 *                </p>
	 */
	public JSONObject sendTextMessage(@NotNull String content, String touser, String toparty, String totag)
	{
		TextMessageContent messageContent = new TextMessageContent();
		messageContent.setContent(content);
		TextMessage message = new TextMessage();
		message.setText(messageContent);
		setSendTo(message, touser, toparty, totag);
		return sendMessage(message);
	}

	/**
	 * @param typeEnum 消息枚举类型
	 * @param fileUrl  文件url
	 * @param touser
	 *                 <p>
	 *                 成员ID列表（多个接收者用‘|’分隔，最多支持1000个）.指定为”@all”，则向该企业应用的全部成员发送
	 *                 </p>
	 * @param toparty
	 *                 <p>
	 *                 部门ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为”@all”时忽略本参数
	 *                 </p>
	 * @param totag
	 *                 <p>
	 *                 标签ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为”@all”时忽略本参数
	 *                 </p>
	 */
	public JSONObject sendOtherTypeMessage(@NotNull AppMessageTypeEnum typeEnum, @NotNull String fileUrl, String touser,
			String toparty, String totag)
	{
		JSONObject result = new JSONObject(4);
		if (typeEnum == AppMessageTypeEnum.Text)
		{
			return JSONObject.parseObject("{\"errcode\":-1,\"errmsg\":\"请选择非文本内容的消息类型枚举\"}");
		}
		result = uploadTempMaterial(typeEnum, fileUrl);
		if (result != null && result.getIntValue("errcode") == 0)
		{
			String media_id = result.getString("media_id");
			switch (typeEnum)
			{
			case Image:
				result = sendImageMessage(media_id, touser, toparty, totag);
				break;
			case Voice:
				result = sendVoiceMessage(media_id, touser, toparty, totag);
				break;
			case File:
				result = sendFileMessage(media_id, touser, toparty, totag);
				break;
			default:
				result = null;
				break;
			}
		}
		return result;
	}

	/**
	 * @param media_id 外部服务器已上传的媒体ID
	 * @param touser
	 * @param toparty
	 * @param totag
	 */
	public JSONObject sendVoiceMessage(@NotNull String media_id, String touser, String toparty, String totag)
	{
		VoiceMessage message = new VoiceMessage();
		setSendTo(message, touser, toparty, totag);
		message.setVoice(putSendMedia(media_id));
		return sendMessage(message);
	}

	public JSONObject sendImageMessage(@NotNull String media_id, String touser, String toparty, String totag)
	{
		ImageMessage message = new ImageMessage();
		setSendTo(message, touser, toparty, totag);
		message.setImage(putSendMedia(media_id));
		return sendMessage(message);
	}

	public JSONObject sendFileMessage(@NotNull String media_id, String touser, String toparty, String totag)
	{
		FileMessage message = new FileMessage();
		setSendTo(message, touser, toparty, totag);
		message.setFile(putSendMedia(media_id));
		return sendMessage(message);
	}

	private void setSendTo(@NotNull BaseMessage message, String touser, String toparty, String totag)
	{
		message.setTouser(touser);
		message.setToparty(toparty);
		message.setTotag(totag);
	}

	private MessageMedia putSendMedia(@NotNull String media_id)
	{
		MessageMedia media = new MessageMedia();
		media.setMedia_id(media_id);
		return media;
	}

	/**
	 * @desc ：上传临时素材
	 *
	 * @param accessToken 接口访问凭证
	 * @param type        媒体文件类型，分别有图片（image）、语音（voice）、视频（video），普通文件(file)
	 * @param fileUrl     本地文件的url。例如 "D:/1.img"。
	 * @return JSONObject 上传成功后，微信服务器返回的参数，有type、media_id 、created_at
	 */
	public JSONObject uploadTempMaterial(@NotNull AppMessageTypeEnum typeEnum, @NotNull String fileUrl)
	{
		String accessToken = gainAccessToken();
		File file = new File(fileUrl);

		String uploadMediaMaterial_url = MessageParamesConfig.uploadMediaMaterial_url
				.replace("ACCESS_TOKEN", accessToken).replace("TYPE", typeEnum.msgType);

		String result = HttpURLConnectionUtils.httpRequestUpload(uploadMediaMaterial_url, file, null);
		result = result.replaceAll("[\\\\]", "");
		return JSONObject.parseObject(result);
	}
}
