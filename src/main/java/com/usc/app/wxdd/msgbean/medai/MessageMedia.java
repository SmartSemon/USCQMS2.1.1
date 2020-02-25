package com.usc.app.wxdd.msgbean.medai;

import lombok.Data;

/**
 * 图片、语音、文件
 *
 * @author semon
 *
 */
@Data
public class MessageMedia
{
	// 是 图片/语音/文件 媒体文件id，可以调用上传临时素材接口获取
	private String media_id;
}
