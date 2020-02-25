package com.usc.app.wxdd.util;

/**
 * @author SEMON
 *
 */
public enum AppMessageTypeEnum
{
	/**
	 * 文本消息
	 */
	Text("text"),
	/**
	 * 图片消息
	 */
	Image("image"),
	/**
	 * 语音消息
	 */
	Voice("voice"),
	/**
	 * 文件消息
	 */
	File("file");

	public String msgType;

	AppMessageTypeEnum(String msgType)
	{
		this.msgType = msgType;
	}
}
