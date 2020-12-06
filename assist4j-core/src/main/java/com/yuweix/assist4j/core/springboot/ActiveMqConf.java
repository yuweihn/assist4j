package com.yuweix.assist4j.core.springboot;


import com.yuweix.assist4j.core.mq.activemq.message.TextSender;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;


/**
 * @author yuwei
 */
public class ActiveMqConf {
	/**
	 * 真正可以产生Connection的ConnectionFactory，由对应的 JMS服务厂商提供
	 */
	@Bean(name = "targetConnectionFactory")
	public ActiveMQConnectionFactory activeMQConnectionFactory(@Value("${mq.broker.url}") String brokerUrl
					, @Value("${mq.user.name}") String userName
					, @Value("${mq.password}") String password) {
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
		activeMQConnectionFactory.setBrokerURL(brokerUrl);
		activeMQConnectionFactory.setUserName(userName);
		activeMQConnectionFactory.setPassword(password);
		return activeMQConnectionFactory;
	}

	/**
	 * ActiveMQ为我们提供了一个PooledConnectionFactory，通过往里面注入一个ActiveMQConnectionFactory
	 * 可以用来将Connection、Session和MessageProducer池化，这样可以大大的减少我们的资源消耗。
	 * 要依赖于 activemq-pool包
	 */
	@Bean(name = "pooledConnectionFactory")
	public PooledConnectionFactory pooledConnectionFactory(@Qualifier("targetConnectionFactory") Object targetConnectionFactory
					, @Value("${mq.pool.max.connections}") int maxConnections) {
		PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
		pooledConnectionFactory.setConnectionFactory(targetConnectionFactory);
		pooledConnectionFactory.setMaxConnections(maxConnections);
		return pooledConnectionFactory;
	}

	/**
	 * Spring用于管理真正的ConnectionFactory的ConnectionFactory
	 */
	@Bean(name = "connectionFactory")
	public SingleConnectionFactory singleConnectionFactory(@Qualifier("pooledConnectionFactory") ConnectionFactory pooledConnectionFactory) {
		SingleConnectionFactory singleConnectionFactory = new SingleConnectionFactory();
		singleConnectionFactory.setTargetConnectionFactory(pooledConnectionFactory);
		return singleConnectionFactory;
	}

	/**
	 * Spring提供的JMS工具类，它可以进行消息发送、接收等
	 */
	@Bean(name = "activeMqJmsTemplate")
	public JmsTemplate jmsTemplate(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
		JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setConnectionFactory(connectionFactory);
		return jmsTemplate;
	}

	@Bean(name = "textSender")
	public TextSender textSender(@Qualifier("activeMqJmsTemplate") JmsTemplate jmsTemplate) {
		TextSender textSender = new TextSender();
		textSender.setJmsTemplate(jmsTemplate);
		return textSender;
	}
}
