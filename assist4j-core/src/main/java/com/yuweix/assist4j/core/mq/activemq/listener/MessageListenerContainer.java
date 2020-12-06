package com.yuweix.assist4j.core.mq.activemq.listener;


import org.springframework.jms.listener.DefaultMessageListenerContainer;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;


/**
 * 队列监听容器
 * @author yuwei
 */
public class MessageListenerContainer extends DefaultMessageListenerContainer {

	public void setQueueName(String queueName) {
		setDestinationName(queueName);
	}

	@Override
	@Resource
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		super.setConnectionFactory(connectionFactory);
	}

	@Override
	@Resource
	public void setMessageListener(Object messageListener) {
		super.setMessageListener(messageListener);
	}
}
