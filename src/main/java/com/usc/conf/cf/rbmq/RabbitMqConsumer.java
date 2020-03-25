package com.usc.conf.cf.rbmq;

public interface RabbitMqConsumer {
	String getExchange();

	void onMessage(String message);

	String getMQType();

	String getRoutingKey();
}
