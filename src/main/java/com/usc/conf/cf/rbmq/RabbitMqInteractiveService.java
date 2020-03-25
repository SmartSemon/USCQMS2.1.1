package com.usc.conf.cf.rbmq;

import org.springframework.amqp.core.AbstractExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMq 总服务
 * 
 * @author Semon
 *
 */
@Configuration
@Slf4j
public class RabbitMqInteractiveService {
	@Autowired
	private RabbitAdmin rabbitAdmin;
	@Autowired
	private RabbitTemplate rabbitTemplate;

	public RabbitAdmin getRabbitAdmin() {
		return rabbitAdmin;
	}

	public RabbitTemplate getRabbitTemplate() {
		return rabbitTemplate;
	}

	/**
	 * 转换Message对象
	 * 
	 * @param messageType 返回消息类型 MessageProperties类中常量
	 * @param msg
	 * @return
	 */
	public Message getMessage(String messageType, Object msg) {
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setContentType(messageType);
		Message message = new Message(msg.toString().getBytes(), messageProperties);
		return message;
	}

	/**
	 * 有绑定Key的Exchange发送
	 * 
	 * @param routingKey
	 * @param msg
	 */
	public void sendMessageToExchange(AbstractExchange exchange, String routingKey, Object msg) {
		Message message = getMessage(MessageProperties.CONTENT_TYPE_JSON, msg);
		rabbitTemplate.send(exchange.getName(), routingKey, message);
	}

	/**
	 * 没有绑定KEY的Exchange发送
	 * 
	 * @param exchange
	 * @param msg
	 */
	public void sendMessageToExchange(TopicExchange topicExchange, AbstractExchange exchange, String msg) {
		addExchange(exchange);
		log.info("RabbitMQ send " + exchange.getName() + "->" + msg);
		rabbitTemplate.convertAndSend(topicExchange.getName(), msg);
	}

	/**
	 * 给queue发送消息
	 * 
	 * @param queueName
	 * @param msg
	 */
	public void sendToQueue(String queueName, String msg) {
		sendToQueue(DirectExchange.DEFAULT, queueName, msg);
	}

	/**
	 * 给direct交换机指定queue发送消息
	 * 
	 * @param directExchange
	 * @param queueName
	 * @param msg
	 */
	public void sendToQueue(DirectExchange directExchange, String queueName, String msg) {
		Queue queue = new Queue(queueName);
		addQueue(queue);
		Binding binding = BindingBuilder.bind(queue).to(directExchange).withQueueName();
		rabbitAdmin.declareBinding(binding);
		// 设置消息内容的类型，默认是 application/octet-stream 会是 ASCII 码值
		rabbitTemplate.convertAndSend(directExchange.getName(), queueName, msg);
	}

	/**
	 * 给queue发送消息
	 * 
	 * @param queueName
	 * @param msg
	 */
	public String receiveFromQueue(String queueName) {
		return receiveFromQueue(DirectExchange.DEFAULT, queueName);
	}

	/**
	 * 给direct交换机指定queue发送消息
	 * 
	 * @param directExchange
	 * @param queueName
	 * @param msg
	 */
	public String receiveFromQueue(DirectExchange directExchange, String queueName) {
		Queue queue = new Queue(queueName);
		addQueue(queue);
		Binding binding = BindingBuilder.bind(queue).to(directExchange).withQueueName();
		rabbitAdmin.declareBinding(binding);
		String messages = (String) rabbitTemplate.receiveAndConvert(queueName);
		System.out.println("Receive:" + messages);
		return messages;
	}

	/**
	 * 创建Exchange
	 * 
	 * @param exchange
	 */
	public void addExchange(AbstractExchange exchange) {
		rabbitAdmin.declareExchange(exchange);
	}

	/**
	 * 删除一个Exchange
	 * 
	 * @param exchangeName
	 */
	public boolean deleteExchange(String exchangeName) {
		return rabbitAdmin.deleteExchange(exchangeName);
	}

	/**
	 * Declare a queue whose name is automatically named. It is created with
	 * exclusive = true, autoDelete=true, and durable = false.
	 * 
	 * @return Queue
	 */
	public Queue addQueue() {
		return rabbitAdmin.declareQueue();
	}

	/**
	 * 创建一个指定的Queue
	 * 
	 * @param queue
	 * @return queueName
	 */
	public String addQueue(Queue queue) {
		return rabbitAdmin.declareQueue(queue);
	}

	/**
	 * Delete a queue.
	 * 
	 * @param queueName the name of the queue.
	 * @param unused    true if the queue should be deleted only if not in use.
	 * @param empty     true if the queue should be deleted only if empty.
	 */
	public void deleteQueue(String queueName, boolean unused, boolean empty) {
		rabbitAdmin.deleteQueue(queueName, unused, empty);
	}

	/**
	 * 删除一个queue
	 * 
	 * @return true if the queue existed and was deleted.
	 * @param queueName
	 */
	public boolean deleteQueue(String queueName) {
		return rabbitAdmin.deleteQueue(queueName);
	}

	/**
	 * 绑定一个队列到一个匹配型交换器使用一个routingKey
	 * 
	 * @param queue      队列
	 * @param exchange   交换机，支持FANOUT、DIRECT、TOPIC
	 * @param routingKey 路由键
	 */
	public void addBinding(Queue queue, AbstractExchange exchange, String routingKey) {
		Binding binding = null;
		String type = exchange.getType();
		switch (type)
		{
		case ExchangeTypes.FANOUT:
			binding = BindingBuilder.bind(queue).to((FanoutExchange) exchange);
			break;
		case ExchangeTypes.DIRECT:
			binding = BindingBuilder.bind(queue).to((DirectExchange) exchange).with(routingKey);
			break;
		case ExchangeTypes.HEADERS:
			// 系统暂时不考虑头交换机,不实现
			break;
		case ExchangeTypes.TOPIC:
			binding = BindingBuilder.bind(queue).to((TopicExchange) exchange).with(routingKey);
			break;

		default:
			break;
		}
		if (binding != null)
		{
			rabbitAdmin.declareBinding(binding);
			// admin.setAutoStartup(true);设置自启
			log.info("创建队列成功,名称：{},类型：{},创建routingKey：{}", queue.getName(), type, routingKey);
		}

	}

	/**
	 * 绑定一个Exchange到一个匹配型Exchange 使用一个routingKey
	 * 
	 * @param exchange
	 * @param topicExchange
	 * @param routingKey
	 */
	public void addBinding(Exchange exchange, TopicExchange topicExchange, String routingKey) {
		Binding binding = BindingBuilder.bind(exchange).to(topicExchange).with(routingKey);
		rabbitAdmin.declareBinding(binding);
	}

	/**
	 * 去掉一个binding
	 * 
	 * @param binding
	 */
	public void removeBinding(Binding binding) {
		rabbitAdmin.removeBinding(binding);
	}
}
