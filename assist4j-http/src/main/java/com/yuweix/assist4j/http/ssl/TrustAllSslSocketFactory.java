package com.yuweix.assist4j.http.ssl;


import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;


/**
 * https绕过证书验证
 * @author yuwei
 */
public class TrustAllSslSocketFactory extends SSLConnectionSocketFactory {
	private static volatile TrustAllSslSocketFactory instance = null;
	private static final String[] SUPPORTED_PROTOCOLS = new String[] {"TLSv1.1", "TLSv1.2"};
	private static final Lock lock = new ReentrantLock();

	private TrustAllSslSocketFactory(SSLContext sslContext) {
		super(sslContext, SUPPORTED_PROTOCOLS, null, getDefaultHostnameVerifier());
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
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
