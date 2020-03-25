package com.usc.conf.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.usc.server.AppRunner;
import com.usc.server.init.InitMqTransactionData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(2)
public class MQ2RedisRunner extends AppRunner {

	@Override
	public void run(ApplicationArguments args) throws Exception {
		try
		{
			Long st = System.currentTimeMillis();
			String condition = "state='F'";
			InitMqTransactionData.init(condition);
			log.info("MQ Transaction init successfully, in " + (System.currentTimeMillis() - st) + " ms");
		} catch (Exception e)
		{
			log.info("MQ Transaction init information failed");
		}

	}

}
