package com.yuweix.assist4j.data.cache;



/**
 * @author yuwei
 */
public interface MessageHandler {
	void handle(String channel, String message);
}
