package com.usc.server.init;

import java.util.List;
import java.util.Map;

import com.usc.app.util.RabbitMqUtils;
import com.usc.app.util.RabbitMqUtilsBuilder;
import com.usc.server.DBConnecter;
import com.usc.util.ObjectHelperUtils;

public class InitMqLines {
	public static void init() {
		String sql = "select A.NAME AS ENAME,B.QUEUES AS QUEUE from usc_model_mq_lines A,usc_model_mq_listener B where A.DEL=0 and B.DEL=0 and A.STATE='F' and B.STATE='F' and A.ID=B.ITEMID";
		List<Map<String, Object>> queryForList = DBConnecter.getJdbcTemplate().queryForList(sql);
		if (ObjectHelperUtils.isNotEmpty(queryForList))
		{
			RabbitMqUtils mqUtils = RabbitMqUtilsBuilder.build();
			for (Map<String, Object> map : queryForList)
			{
				String ename = map.get("ENAME").toString();
				mqUtils.deleteExchange(ename);
				String queue = map.get("QUEUE").toString();
				mqUtils.bindQueueWithFanoutExchange(ename, queue);
			}
		}
	}
}
