package com.yuweix.assist4j.core.mq.activemq.message;




/**
 * 消息接收器接口
 * @author yuwei
 */
public interface IReceiver {
	void receive(String channel, String message);
}
