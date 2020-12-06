package com.yuweix.assist4j.http.strategy.redirect;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;


/**
 * 重定向策略(不重定向)
 * @author yuwei
 */
public class NotNeedRedirectStrategy implements RedirectStrategy {
	private static volatile NotNeedRedirectStrategy instance = null;
	private static final Lock lock = new ReentrantLock();


	private NotNeedRedirectStrategy() {
		
	}

	public static NotNeedRedirectStrategy get() {
		if (instance == null) {
			lock.lock();
			try {
				if (instance == null) {
					instance = new NotNeedRedirectStrategy();
				}
			} finally {
				lock.unlock();
			}
		}

		return instance;
	}

	public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
		return false;
	}

	@Override
	public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
		return null;
	}
}
