package com.usc.conf.cf.rbmq.listener;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.Channel;
import com.usc.server.DBConnecter;

@Configuration
public class TestMQReceiver {
	@RabbitListener(queues = "Queue_01")
	public void handleMessage(Message message, Channel channel) {
		String body = new String(message.getBody());

		System.out.println("监听1消费消息：" + body);
		DBConnecter.getJdbcTemplate().execute(body);
		MessageProperties properties = message.getMessageProperties();
		System.out.println(properties.toString());
		long deliveryTag = properties.getDeliveryTag();
		try
		{
			channel.basicAck(deliveryTag, true);
			Thread.sleep(100);
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

	}

}
