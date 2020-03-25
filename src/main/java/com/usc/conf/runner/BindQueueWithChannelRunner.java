package com.usc.conf.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.usc.server.AppRunner;
import com.usc.server.init.InitMqLines;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(1)
public class BindQueueWithChannelRunner extends AppRunner {

	@Override
	public void run(ApplicationArguments args) throws Exception {
		InitMqLines.init();
		log.info("MQ initialized successfully");
	}

}
