package com.usc.conf.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.usc.server.AppRunner;
import com.usc.server.init.InitModelData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(1)
public class ModelData2RedisRunner extends AppRunner
{

	public void run(ApplicationArguments args) throws Exception
	{
		try
		{
			Long st = System.currentTimeMillis();
			InitModelData.initModel();
			log.info("Initialize system model successfully, in " + (System.currentTimeMillis() - st) + " ms");
		} catch (Exception e)
		{
			log.info("Initialize system model failded", e);
		}

	}

}
