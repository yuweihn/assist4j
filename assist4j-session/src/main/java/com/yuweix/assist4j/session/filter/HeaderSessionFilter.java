package com.yuweix.assist4j.session.filter;


import com.yuweix.assist4j.session.cache.SessionCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 从header中取出某个值作为sessionId
 * @author yuwei
 */
public class HeaderSessionFilter extends CookieSessionFilter {
	protected String key;
	protected boolean responseHeader;


	public HeaderSessionFilter(SessionCache cache) {
		super(cache);
	}
	public HeaderSessionFilter() {
		super();
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setResponseHeader(boolean responseHeader) {
		this.responseHeader = responseHeader;
	}



	@Override
	protected String getSessionId(HttpServletRequest request, HttpServletResponse response) {
		if (key == null || "".equals(key)) {
			throw new RuntimeException("[key]不能为空！！！");
		}

		String sessionId = request.getHeader(key);
		if (sessionId == null || "".equals(sessionId.trim())) {
			throw new RuntimeException("[sessionId]不能为空！！！");
		}
		if (responseHeader) {
			response.setHeader(key, sessionId);
		}
		return sessionId;
	}
}
