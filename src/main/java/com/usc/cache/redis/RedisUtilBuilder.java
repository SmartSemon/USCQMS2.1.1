package com.usc.cache.redis;

import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;

public class RedisUtilBuilder
{
	public static Integer dataBaseIndex = null;;
	public static boolean isStringTemplate = false;

	@Async
	public static RedisUtil builder()
	{
		RedisUtil redisUtil = getInstanceOfObject();
		if (isStringTemplate)
		{
			redisUtil = getInstanceOfString();
		}

		if (dataBaseIndex != null)
		{
			changDataBase(redisUtil, dataBaseIndex);
			setDataBaseIndex(null);
		}

		return redisUtil;
	}

	private static void changDataBase(RedisUtil redisUtil, Integer dataBaseIndex)
	{
		RedisTemplate template = redisUtil.getRedisTemplate();
		LettuceConnectionFactory redisConnectionFactory = (LettuceConnectionFactory) template.getConnectionFactory();
		redisConnectionFactory.setDatabase(dataBaseIndex);
		template.setConnectionFactory(redisConnectionFactory);
	}

	public static RedisUtil getInstanceOfObject()
	{
		return new RedisUtil(false);
	}

	public static RedisUtil getInstanceOfString()
	{
		return new RedisUtil(true);
	}

	public static void setDataBaseIndex(Integer index)
	{
		dataBaseIndex = index;
	}

	public static void setIsStringTemplate(boolean b)
	{
		isStringTemplate = b;
	}
}
