package com.usc.app.wxdd.msgbean.text;

/**
 * <p>
 * 文本消息体
 * </p>
 *
 * @author SEMON
 *
 */
public class TextMessageContent
{
	/**
	 * 消息内容，最长不超过2048个字节
	 */
	private String content;

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}
}
