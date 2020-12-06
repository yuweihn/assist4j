package com.yuweix.assist4j.core.mq.activemq.message;




/**
 * 消息接收器接口
 * @author wei
 */
public interface IReceiver {
	void receive(String channel, String message);
}
