package com.usc.server.init;

import java.util.List;

import com.usc.cache.redis.RedisUtil;
import com.usc.server.DBConnecter;
import com.usc.server.mq.ItemMQMenu;
import com.usc.server.mq.mapper.ItemMQMenuRowMapper;
import com.usc.util.ObjectHelperUtils;

public class InitMqTransactionData {

	private static final String MQAFFAIRSQL = "SELECT id,no,name,mtype,mcontent,reqparam,channelname FROM usc_model_mq_affair where del=0";
	private static final String ITEM_MQAFFAIRS = "MODEL_MQAFFAIRS";

	public static void init(String condition) {
		RedisUtil redisUtil = RedisUtil.getInstanceOfObject();
		List<ItemMQMenu> mqMenus = DBConnecter.getJdbcTemplate().query(MQAFFAIRSQL + " AND " + condition,
				new ItemMQMenuRowMapper());
		if (ObjectHelperUtils.isNotEmpty(mqMenus))
		{
			redisUtil.del(ITEM_MQAFFAIRS);
			for (ItemMQMenu itemMQMenu : mqMenus)
			{
				String no = itemMQMenu.getNo();
				redisUtil.hset(ITEM_MQAFFAIRS, no, itemMQMenu);
			}
		}
	}
}
