package com.yuweix.assist4j.http.ssl;


import java.security.cert.X509Certificate;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;


/**
 * 1、不进行主机名验证
 * 2、不进行SSL信任验证
 * @author yuwei
 */
public class TrustAllSslSocketFactory extends SSLConnectionSocketFactory {
	private static volatile TrustAllSslSocketFactory instance = null;
	private static final String[] SUPPORTED_PROTOCOLS = new String[] {"TLSv1.1", "TLSv1.2"};
	private static final Lock lock = new ReentrantLock();

	private TrustAllSslSocketFactory(SSLContext sslContext) {
		super(sslContext, SUPPORTED_PROTOCOLS, null, NoopHostnameVerifier.INSTANCE);
	}

	public static TrustAllSslSocketFactory get() {
		if (instance == null) {
			lock.lock();
			try {
				if (instance == null) {
					instance = new TrustAllSslSocketFactory(createSslContext());
				}
			} finally {
				lock.unlock();
			}
		}

		return instance;
	}

	/**
	 * 不对主机名进行验证
	 */
	private static SSLContext createSslContext() {
		try {
			return new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) {
					return true;
				}
			}).build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
