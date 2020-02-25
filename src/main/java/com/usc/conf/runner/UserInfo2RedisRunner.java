package com.usc.conf.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.usc.server.AppRunner;
import com.usc.server.init.InitUserInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(2)
public class UserInfo2RedisRunner extends AppRunner
{

	@Override
	public void run(ApplicationArguments args) throws Exception
	{
		try
		{
			Long st = System.currentTimeMillis();
			InitUserInfo.run();
			log.info(
					"Initialization of user information successfully, in " + (System.currentTimeMillis() - st) + " ms");
		} catch (Exception e)
		{
			log.info("Initialization of user information failed");
		}

	}

}
