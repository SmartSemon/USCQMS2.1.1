package com.usc.app.util;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.usc.conf.cf.rbmq.RabbitMqInteractiveService;
import com.usc.util.SpringContextUtil;

/**
 * MQ工具构造器
 * 
 * @author Semon
 *
 */
public class RabbitMqUtilsBuilder {
	public static RabbitMqUtils build() {
		return new RabbitMqUtils(buildService());
	}

	public static RabbitTemplate buildRabbitTemplate() {
		return SpringContextUtil.getBean(RabbitTemplate.class);
	}

	public static ConnectionFactory buildConnectionFactory() {
		return SpringContextUtil.getBean(CachingConnectionFactory.class);
	}

	public static RabbitMqInteractiveService buildService() {
		return SpringContextUtil.getBean(RabbitMqInteractiveService.class);
	}
}
