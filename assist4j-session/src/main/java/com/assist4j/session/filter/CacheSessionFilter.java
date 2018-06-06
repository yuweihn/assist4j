package com.assist4j.session.filter;


import java.io.IOException;
import java.lang.reflect.Constructor;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.assist4j.session.CacheSessionHttpServletRequest;
import com.assist4j.session.CacheSessionUtil;
import com.assist4j.session.cache.SessionCache;
import com.assist4j.session.CacheSessionConstants;


/**
 * @author yuwei
 */
public class CacheSessionFilter implements Filter {
	/**
	 * session有效期(分钟)
	 */
	private int maxInactiveInterval;
	private SessionCache cache;
	/**
	 * 缓存中session对象的key的前缀
	 */
	private String cacheSessionKey;
	/**
	 * Cookie中保存sessionId的属性名称
	 */
	private String cookieSessionName;


	/**
	 * @param cache                           缓存引擎
	 * @param cacheSessionKey                 缓存中session对象的key的前缀
	 * @param maxInactiveInterval             session有效期(分钟)
	 * @param cookieSessionName               Cookie中保存sessionId的属性名称
	 */
	public CacheSessionFilter(SessionCache cache, String cacheSessionKey, int maxInactiveInterval, String cookieSessionName) {
		this.cache = cache;
		this.cacheSessionKey = cacheSessionKey;
		this.maxInactiveInterval = maxInactiveInterval;
		this.cookieSessionName = cookieSessionName;
		initCacheSessionUtil(cache);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		CacheSessionHttpServletRequest cacheRequest = new CacheSessionHttpServletRequest(httpRequest, httpResponse, cacheSessionKey, cache);
		cacheRequest.setSessionCookieName(cookieSessionName);
		cacheRequest.setMaxInactiveInterval(maxInactiveInterval);

		chain.doFilter(cacheRequest, httpResponse);
		cacheRequest.syncSessionToCache();
	}

	@Override
	public void init(FilterConfig config) {
		
	}

	@Override
	public void destroy() {
		
	}

	/**
	 * 初始化{@link CacheSessionUtil.instance}
	 * @param cache
	 */
	private void initCacheSessionUtil(SessionCache cache) {
		try {
			Class<?> clz = Class.forName(CacheSessionUtil.class.getName());
			Constructor<?> constructor = clz.getDeclaredConstructor(SessionCache.class, String.class);
			constructor.setAccessible(true);
			constructor.newInstance(cache, cacheSessionKey + "." + CacheSessionConstants.SESSION_ID_KEY_CURRENT);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
