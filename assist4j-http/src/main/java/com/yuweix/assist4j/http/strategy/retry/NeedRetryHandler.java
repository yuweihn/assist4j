package com.yuweix.assist4j.http.strategy.retry;


import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HttpContext;


/**
 * 异常恢复机制 连接失败后，可以针对相应的异常进行相应的处理措施；
 * @author yuwei
 */
public class NeedRetryHandler implements HttpRequestRetryHandler {
	private static volatile NeedRetryHandler instance = null;
	private static final Lock lock = new ReentrantLock();

	private NeedRetryHandler() {
		
	}

	public static NeedRetryHandler get() {
		if (instance == null) {
			lock.lock();
			try {
				if (instance == null) {
					instance = new NeedRetryHandler();
				}
			} finally {
				lock.unlock();
			}
		}

		return instance;
	}

	@Override
	public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
		/**
		 * 如果连接次数超过5次，就不进行重复连接
		 */
		if (executionCount >= 5) {
			return false;
		}
		/**
		 * io操作中断
		 */
		if (exception instanceof InterruptedIOException) {
			return false;
		}
		/**
		 * 未找到主机
		 */
		if (exception instanceof UnknownHostException) {
			return false;
		}
		/**
		 * 连接超时
		 */
		if (exception instanceof ConnectTimeoutException) {
			return true;
		}
		/**
		 * SSL handshake exception
		 */
		if (exception instanceof SSLException) {
			return false;
		}
		HttpClientContext clientContext = HttpClientContext.adapt(context);
		HttpRequest request = clientContext.getRequest();
		boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);

		/**
		 * Retry if the request is considered idempotent
		 */
		if (idempotent) {
			return true;
		}
		return false;
	}
}
