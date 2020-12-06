package com.yuweix.assist4j.http.strategy.retry;


import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;


/**
 * 不重试
 * @author yuwei
 */
public class NotNeedRetryHandler implements HttpRequestRetryHandler {
	private static volatile NotNeedRetryHandler instance = null;
	private static final Lock lock = new ReentrantLock();

	private NotNeedRetryHandler() {
		
	}

	public static NotNeedRetryHandler get() {
		if (instance == null) {
			lock.lock();
			try {
				if (instance == null) {
					instance = new NotNeedRetryHandler();
				}
			} finally {
				lock.unlock();
			}
		}

		return instance;
	}

	@Override
	public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
		return false;
	}
}
