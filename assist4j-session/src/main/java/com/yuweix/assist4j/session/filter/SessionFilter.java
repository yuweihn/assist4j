package com.yuweix.assist4j.session.filter;


import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yuweix.assist4j.session.CacheHttpServletRequest;
import com.yuweix.assist4j.session.CookiesUtil;
import com.yuweix.assist4j.session.SessionConstant;
import com.yuweix.assist4j.session.cache.SessionCache;
import com.yuweix.assist4j.session.conf.PathPattern;
import com.yuweix.assist4j.session.conf.SessionConf;


/**
 * @author yuwei
 */
public class SessionFilter implements Filter {
	/**
	 * 需要排除的URI
	 */
	private static final String EXCLUSIVE = "exclusive";


	/**
	 * @param cache                           缓存引擎
	 */
	public SessionFilter(SessionCache cache) {
		setCache(cache);
	}
	public SessionFilter() {

	}

	public void setCache(SessionCache cache) {
		SessionConf.getInstance().setCache(cache);
	}
	/**
	 * 设置session失效时间(分钟)
	 */
	public void setMaxInactiveInterval(int maxInactiveInterval) {
		SessionConf.getInstance().setMaxInactiveInterval(maxInactiveInterval);
	}
	public void setApplicationName(String applicationName) {
		SessionConf.getInstance().setApplicationName(applicationName);
	}


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		PathPattern exclusivePattern = SessionConf.getInstance().getExclusivePattern();
		if (exclusivePattern != null && exclusivePattern.matches(httpRequest)) {
			chain.doFilter(request, response);
			return;
		}

		before(httpRequest, httpResponse);
		CacheHttpServletRequest cacheRequest = new CacheHttpServletRequest(httpRequest, getSessionId(httpRequest, httpResponse));

		chain.doFilter(cacheRequest, httpResponse);
		cacheRequest.sync();
		after(cacheRequest, httpResponse);
	}

	protected String getSessionId(HttpServletRequest request, HttpServletResponse response) {
		String sid = CookiesUtil.findValueByKey(request
				, SessionConf.getInstance().getApplicationName() + SessionConstant.COOKIE_SESSION_ID_SUFFIX);
		if (sid == null || "".equals(sid)) {
			sid = generateSessionId(request, response);
		}
		return sid;
	}

	private String generateSessionId(HttpServletRequest request, HttpServletResponse response) {
		String sid = UUID.randomUUID().toString().replace("-", "");
		if (sid == null || "".equals(sid)) {
			throw new RuntimeException("生成SessionId失败！！！");
		}
		addCookie(sid, request, response);
		return sid;
	}

	/**
	 * 更新cookie值
	 */
	private void addCookie(String sid, HttpServletRequest request, HttpServletResponse response) {
		CookiesUtil.addCookie(request, response
				, SessionConf.getInstance().getApplicationName() + SessionConstant.COOKIE_SESSION_ID_SUFFIX
				, sid,null,null, SessionConstant.COOKIE_MAX_AGE_DEFAULT);
	}

	protected void before(HttpServletRequest request, HttpServletResponse response) {

	}

	protected void after(HttpServletRequest request, HttpServletResponse response) {

	}

	@Override
	public void init(FilterConfig config) {
		SessionConf sessionConf = SessionConf.getInstance();

		String exclusive = config.getInitParameter(EXCLUSIVE);
		if (exclusive != null && !"".equals(exclusive.trim())) {
			sessionConf.setExclusivePattern(exclusive.split(","));
		}

		sessionConf.check();
	}

	@Override
	public void destroy() {

	}
}
