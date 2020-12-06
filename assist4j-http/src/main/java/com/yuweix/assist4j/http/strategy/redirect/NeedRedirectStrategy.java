package com.yuweix.assist4j.http.strategy.redirect;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.http.impl.client.DefaultRedirectStrategy;


/**
 * 重定向策略(重定向)
 * @author yuwei
 */
public class NeedRedirectStrategy extends DefaultRedirectStrategy {
	private static volatile NeedRedirectStrategy instance = null;
	private static final Lock lock = new ReentrantLock();

	private NeedRedirectStrategy() {
		
	}

	public static NeedRedirectStrategy get() {
		if (instance == null) {
			lock.lock();
			try {
				if (instance == null) {
					instance = new NeedRedirectStrategy();
				}
			} finally {
				lock.unlock();
			}
		}

		return instance;
	}
}
