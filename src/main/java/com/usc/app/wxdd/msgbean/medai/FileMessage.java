package com.usc.app.wxdd.msgbean.medai;

import com.usc.app.wxdd.msgbean.base.BaseMessage;

/**
 * 文件消息
 *
 * @author semon
 *
 */
public class FileMessage extends BaseMessage
{

	// 文件
	private MessageMedia file;
	// 否 表示是否是保密消息，0表示否，1表示是，默认0
	private int safe;

	public FileMessage()
	{
		setMsgtype("file");
	}

	public MessageMedia getFile()
	{
		return file;
	}

	public void setFile(MessageMedia file)
	{
		this.file = file;
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
