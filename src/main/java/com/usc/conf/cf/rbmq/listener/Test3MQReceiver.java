package com.usc.conf.cf.rbmq.listener;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import com.rabbitmq.client.Channel;

//@Component
public class Test3MQReceiver {
	@RabbitListener(queues = { "Queue_03" })
	public void handleMessage(Message message, Channel channel) {

		System.err.println("监听3消费消息：" + new String(message.getBody()));
		MessageProperties properties = message.getMessageProperties();
//		System.out.println(properties);
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
