package com.usc.app.util;

import org.springframework.amqp.core.AbstractExchange;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.usc.conf.cf.rbmq.RabbitMqInteractiveService;
import com.usc.conf.cf.rbmq.RabbitMqSendServer;
import com.usc.util.SpringContextUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * MQ 交换机、队列的增删和绑定，以及发送消息
 * 
 * @author Semon
 *
 */
@Slf4j
public class RabbitMqUtils
		implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback, RabbitMqSendServer {

	private RabbitMqInteractiveService service;

	/************ Send_Topic exchanges 主题交换机 ***********************/
	private static final String TOPIC_EXCHANGE = "usc_default_topic_exchange";
	private static final String CASHIER_EXCHANE = "usc_default_cashier_exchange";

	public RabbitMqUtils(RabbitMqInteractiveService service)
	{
		this.service = service;
		SpringContextUtil.getBean(RabbitTemplate.class).setConfirmCallback(this);// 最后设置内容
	}

	/**
	 * 声明一个交换机
	 * 
	 * @param exchangeName 交换机名称
	 * @param exchangeType 交换机类型
	 * @see ExchangeTypes
	 * @return
	 */
	public boolean declareExchange(String exchangeName, String exchangeType) {
		AbstractExchange exchange = null;
		switch (exchangeType)
		{
		case ExchangeTypes.DIRECT:
			exchange = instanceDirectExchange(exchangeName);
			break;
		case ExchangeTypes.FANOUT:
			exchange = instanceFanoutExchange(exchangeName);
			break;
		case ExchangeTypes.TOPIC:
			exchange = instanceTopicExchange(exchangeName);
			break;

		default:
			break;
		}
		service.addExchange(exchange);
		return true;
	}

	/**
	 * 删除交换机
	 * 
	 * @param exchangeName 交换机名
	 * @return
	 */
	public boolean deleteExchange(String exchangeName) {
		return service.deleteExchange(exchangeName);
	}

	/**
	 * 声明指定名称队列
	 * 
	 * @param queueName 队列名
	 */
	public Queue declareQueue(String queueName) {
		Queue queue = instanceQueue(queueName);
		service.addQueue(queue);
		return queue;
	}

	/**
	 * 声明队列并绑定默认交换机
	 * 
	 * @param queueName  队列名
	 * @param routingKey 路由键
	 */
	public void bindQueue(String queueName, String routingKey) {
		Queue queue = declareQueue(queueName);
		AbstractExchange topicExchange = instanceDefaultTopicExchange();
		service.addExchange(topicExchange);
		service.addBinding(queue, topicExchange, routingKey);
	}

	/**
	 * 声明队列并绑定指定交换机
	 * 
	 * @param exchangeName 交换机名
	 * @param queueName    队列名
	 * @param routingKey   路由键
	 */
	public void bindQueueWithTopicExchange(String exchangeName, String queueName, String routingKey) {
		Queue queue = declareQueue(queueName);
		AbstractExchange topicExchange = instanceTopicExchange(exchangeName);
		service.addExchange(topicExchange);
		service.addBinding(queue, topicExchange, routingKey);
	}

	/**
	 * 声明队列并绑定指定交换机
	 * 
	 * @param exchangeName 交换机名
	 * @param queueName    队列名
	 */
	public void bindQueueWithFanoutExchange(String exchangeName, String queueName) {
		Queue queue = declareQueue(queueName);
		AbstractExchange topicExchange = instanceFanoutExchange(exchangeName);
		service.addExchange(topicExchange);
		service.addBinding(queue, topicExchange, "");
	}

	/**
	 * 声明队列并绑定指定交换机
	 * 
	 * @param exchangeName 交换机名
	 * @param queueName    队列名
	 */
	public void bindQueueWithDiretExchange(String exchangeName, String queueName) {
		Queue queue = declareQueue(queueName);
		AbstractExchange topicExchange = instanceDirectExchange(exchangeName);
		service.addExchange(topicExchange);
		service.addBinding(queue, topicExchange, "");
	}

	/**
	 * 删除队列
	 * 
	 * @param queueName 队列名
	 * @return
	 */
	public boolean deleteQueue(String queueName) {
		boolean result = service.deleteQueue(queueName);
		if (result)
		{
			log.info("删除队列{}成功", queueName);
		} else
		{
			log.info("删除队列{}失败", queueName);
		}
		return result;
	}

	/**
	 * 解除队列和路由绑定
	 * 
	 * @param queueName  队列名
	 * @param routingKey 路由键
	 */
	public void removeBinding(String queueName, String routingKey) {
		TopicExchange topicExchange = instanceDefaultTopicExchange();
		service.removeBinding(BindingBuilder.bind(instanceQueue(queueName)).to(topicExchange).with(routingKey));
	}

	/**
	 * 实例化队列
	 * 
	 * @param queueName
	 * @return
	 */
	private Queue instanceQueue(String queueName) {
		return new Queue(queueName);
	}

	/**
	 * 实例化默认的主题交换机
	 * 
	 * @return
	 */
	private TopicExchange instanceDefaultTopicExchange() {
		return new TopicExchange(TOPIC_EXCHANGE);
	}

	/**
	 * 实例化主题交换机
	 * 
	 * @return
	 */
	private TopicExchange instanceTopicExchange(String exchangeName) {
		return new TopicExchange(exchangeName);
	}

	/**
	 * 实例化扇形交换机（广播模式）
	 * 
	 * @return
	 */
	private FanoutExchange instanceFanoutExchange(String exchangeName) {
		return new FanoutExchange(exchangeName);
	}

	/**
	 * 实例化直接交换机
	 * 
	 * @return
	 */
	private DirectExchange instanceDirectExchange(String exchangeName) {
		return new DirectExchange(exchangeName);
	}

	/**
	 * 发送一个单体配置消息
	 * 
	 * @param message 消息实体
	 */
	@Override
	public void send(Object message) throws Exception {
//		service.sendToQueue(queueName, msg);
	}

	@Override
	public void sendToQueue(String queueName, String message) throws Exception {
		service.sendToQueue(queueName, message);
	}

	@Override
	public void sendToTopicExchange(String exchangeName, String routingkey, String message) throws Exception {
		AbstractExchange exchange = instanceTopicExchange(exchangeName);
		service.sendMessageToExchange(exchange, routingkey, message);
	}

	@Override
	public void sendToFanoutExchange(String exchangeName, String message) throws Exception {
		AbstractExchange exchange = instanceFanoutExchange(exchangeName);
		service.sendMessageToExchange(exchange, "", message);
	}

	@Override
	public void sendToDirectExchange(String exchangeName, String routingkey, String message) throws Exception {
		AbstractExchange exchange = instanceDirectExchange(exchangeName);
		service.sendMessageToExchange(exchange, routingkey, message);
	}

	/**
	 * 发送一个指定队列路由键的消息
	 * 
	 * @param routingKey 路由键
	 * @param msg        消息实体/推荐类型 org.springframework.amqp.core.Message
	 */
	@Override
	public void send(String routingKey, Object msg) throws Exception {
		this.send(TOPIC_EXCHANGE, routingKey, msg);
	}

	/**
	 * 发送一个指定交换机指定队列路由键的消息
	 * 
	 * @param exchange   交换机
	 * @param routingKey 路由键
	 * @param msg        消息实体/推荐类型 org.springframework.amqp.core.Message
	 */
	@Override
	public void send(String exchange, String routingKey, Object msg) throws Exception {
		service.sendMessageToExchange(new TopicExchange(exchange), routingKey, msg);
//		rabbitTemplate.convertAndSend(exchange, routingKey, msg, new CorrelationData(UUID.randomUUID().toString()));
	}

	/**
	 * 确认发送消息
	 */
	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		if (correlationData == null)
		{ return; }
		log.debug("send id:" + correlationData.getId());
		if (ack)
		{// 调用成功
			log.warn(correlationData.getId() + ":" + "发送成功.");
		} else
		{
			log.warn(correlationData.getId() + ":" + "发送失败.");
		}
	}

	/**
	 * 获取返回值
	 */
	@Override
	public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
		log.info("send message failed:" + "{message:" + message + ",replyCode:" + replyCode + ",replyText:" + replyText
				+ ",exchange" + exchange + ",routingKey" + routingKey + "}");
	}

	public void sendCashierMsg(String routingKey, String msg) throws Exception {
		send(CASHIER_EXCHANE, routingKey, msg);
	}
}
