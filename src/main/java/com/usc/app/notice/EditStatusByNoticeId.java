package com.usc.app.notice;

import com.usc.app.action.a.AbstractAction;
import com.usc.obj.api.USCObject;
import com.usc.obj.util.USCObjectQueryHelper;

/**
 * @Author: lwp
 * @DATE: 2019/12/21 16:05
 * @Description: 根据消息（通知...）标记为已读
 **/
public class EditStatusByNoticeId extends AbstractAction
{
	@Override
	public Object executeAction() throws Exception
	{
		// 根据id获取notice
		String noticeId = (String) context.getExtendInfo("noticeId");
		USCObject notice = USCObjectQueryHelper.getObjectByID("NOTICE", noticeId);
		if (notice != null)
		{
			// 修改notice为已读
			notice.setFieldValue("STATUS", 1);
			notice.save(context);
		}

		return successfulOperation();
	}

	@Override
	public boolean disable() throws Exception
	{
		return false;
	}
}
