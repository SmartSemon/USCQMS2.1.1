package com.usc.app.wxdd.msgbean.medai;

import com.usc.app.wxdd.msgbean.base.BaseMessage;

/**
 * 语音消息
 *
 * @author semon
 *
 */
public class VoiceMessage extends BaseMessage
{
	// 语音
	private MessageMedia voice;
	// 否 表示是否是保密消息，0表示否，1表示是，默认0
	private int safe;

	public VoiceMessage()
	{
		setMsgtype("voice");
	}

	public MessageMedia getVoice()
	{
		return voice;
	}

	public void setVoice(MessageMedia voice)
	{
		this.voice = voice;
	}

	public int getSafe()
	{
		return safe;
	}

	public void setSafe(int safe)
	{
		this.safe = safe;
	}
}
