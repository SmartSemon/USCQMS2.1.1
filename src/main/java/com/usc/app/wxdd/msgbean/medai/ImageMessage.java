package com.usc.app.wxdd.msgbean.medai;

import com.usc.app.wxdd.msgbean.base.BaseMessage;

/**
 * 图片消息
 *
 * @author semon
 *
 */
public class ImageMessage extends BaseMessage
{
	// 图片
	private MessageMedia image;
	// 否 表示是否是保密消息，0表示否，1表示是，默认0
	private int safe;

	public ImageMessage()
	{
		setMsgtype("image");
	}

	public MessageMedia getImage()
	{
		return image;
	}

	public void setImage(MessageMedia image)
	{
		this.image = image;
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
