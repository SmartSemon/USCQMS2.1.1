package com.usc.app.wxdd.msgbean.text;

import com.usc.app.wxdd.msgbean.base.BaseMessage;

import lombok.Data;

/**
 * 文本消息
 *
 * @author semon
 *
 */
@Data
public class TextMessage extends BaseMessage
{
	// 文本
	private TextMessageContent text;
	// 否 表示是否是保密消息，0表示否，1表示是，默认0
	private int safe;

	public TextMessage()
	{
		setMsgtype("text");
	}
}
