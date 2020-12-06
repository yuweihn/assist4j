package com.yuweix.assist4j.http;


import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;


/**
 * HTTP请求过滤器，在执行请求之前拦截HttpRequest和 HttpContext；
 * @author yuwei
 */
public class DefaultHttpRequestInterceptor implements HttpRequestInterceptor {
	private static volatile DefaultHttpRequestInterceptor instance = null;
	private static final Lock lock = new ReentrantLock();

	private DefaultHttpRequestInterceptor() {
		
	}

	public static DefaultHttpRequestInterceptor get() {
		if (instance == null) {
			lock.lock();
			try {
				if (instance == null) {
					instance = new DefaultHttpRequestInterceptor();
				}
			} finally {
				lock.unlock();
			}
		}
		
		return instance;
	}


	@Override
	public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
		
	}
}
