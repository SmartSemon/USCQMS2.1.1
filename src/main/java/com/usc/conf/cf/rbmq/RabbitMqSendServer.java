package com.usc.conf.cf.rbmq;

public interface RabbitMqSendServer {
	void send(Object message) throws Exception;

	void send(String routingKey, Object message) throws Exception;

	void send(String exchange, String routingKey, Object message) throws Exception;

	void sendToQueue(String queueName, String message) throws Exception;

	void sendToTopicExchange(String exchangeName, String routingkey, String message) throws Exception;

	void sendToFanoutExchange(String exchangeName, String message) throws Exception;

	void sendToDirectExchange(String exchangeName, String routingkey, String message) throws Exception;
}
