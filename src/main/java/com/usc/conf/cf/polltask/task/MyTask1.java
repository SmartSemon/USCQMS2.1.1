package com.usc.conf.cf.polltask.task;

import java.sql.Timestamp;
import java.util.Date;

public class MyTask1 extends PollTaskJob
{

	@Override
	public void doTaskThings()
	{
		Date now = new Date();
		Timestamp timestamp = new Timestamp(now.getTime());
		String t = timestamp.toString();
		PollTaskJob.LOGGER.info(this.getClass().getName() + "-----------------正在扫描预警信息-------------------" + "[" + t + "]");
		USCObject[] objects = USCObjectQueryHelper.getObjectsByCondition("EARLYWARNING", "DEL=0 and DWSTATE='A'");
		if (objects != null) 
		{
			for (USCObject uscObject : objects) 
			{
				Date ntime = uscObject.getFieldValueToDate("NTIME");
				Long long1 = (now.getTime()-ntime.getTime())/1000;
				int cycle = uscObject.getFieldValueToInteger("CYCLE");
				if (long1.intValue()>=cycle) {
					WxDdMessageService wxddService = SpringContextUtil.getBean(WxDdMessageService.class);
					wxddService.sendTextMessage("质量系统预警提醒:\r\n\t内容："+uscObject.getFieldValueToString("NAME")+
							"\r\n\t严重程度："+uscObject.getFieldValueToString("SEVERITY"), uscObject.getFieldValueToString("DWUSER"), null, null);
					uscObject.setFieldValue("NTIME", timestamp);
					uscObject.save(createSyatemConext(uscObject));
				}
			}
		}
	}

}
