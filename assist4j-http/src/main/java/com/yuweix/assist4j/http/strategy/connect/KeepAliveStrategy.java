package com.yuweix.assist4j.http.strategy.connect;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;


/**
 * 设置长连接策略，根据服务器的约束或者客户端的约束来设置长连接的时长；
 * @author yuwei
 */
public class KeepAliveStrategy extends DefaultConnectionKeepAliveStrategy {
	private static volatile KeepAliveStrategy instance = null;
	private static final Lock lock = new ReentrantLock();
	
	private KeepAliveStrategy() {
		
	}
	
	public static KeepAliveStrategy get() {
		if (instance == null) {
			lock.lock();
			try {
				if (instance == null) {
					instance = new KeepAliveStrategy();
				}
			} finally {
				lock.unlock();
			}
		}
		
		return instance;
	}


	/**
	 * 服务器端配置（以tomcat为例）：keepAliveTimeout=60000，表示在60s内内，服务器会一直保持连接状态。
	 * 也就是说，如果客户端一直请求服务器，且间隔未超过60s，则该连接将一直保持，如果60s内未请求，则超时。
	 * 
	 * getKeepAliveDuration返回超时时间；
	 */
	@Override
	public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
		/**
		 * 如果服务器指定了超时时间，则以服务器的超时时间为准
		 */
		HeaderElementIterator itr = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
		while (itr.hasNext()) {
			HeaderElement ele = itr.nextElement();
			String param = ele.getName();
			String value = ele.getValue();
			if (value != null && "timeout".equalsIgnoreCase(param)) {
				try {
					return Long.parseLong(value) * 1000;
				} catch (NumberFormatException e) {
					throw new RuntimeException("Invalid value of parameter timeout, that is [" + value  + "].", e);
				}
			}
		}
		
		long keepAlive = super.getKeepAliveDuration(response, context);
		
		/**
		 * 如果服务器未指定超时时间，则客户端默认30s超时
		 */
		if (keepAlive == -1) {
			keepAlive = 30L * 1000;
		}
		
		return keepAlive;
	}
}
