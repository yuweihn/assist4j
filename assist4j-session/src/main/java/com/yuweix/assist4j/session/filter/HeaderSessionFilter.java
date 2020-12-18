package com.yuweix.assist4j.session.filter;


import com.yuweix.assist4j.session.SessionConstant;
import com.yuweix.assist4j.session.cache.SessionCache;
import com.yuweix.assist4j.session.conf.SessionConf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


/**
 * 从header中取出某个值作为sessionId
 * @author yuwei
 */
public class HeaderSessionFilter extends AbstractSessionFilter {
	private String key;


	public HeaderSessionFilter(SessionCache cache) {
		super(cache);
	}
	public HeaderSessionFilter() {
		super();
	}

	public void setKey(String key) {
		this.key = key;
	}


	@Override
	protected String getSessionId(HttpServletRequest request, HttpServletResponse response) {
		String headerKey = key;
		if (headerKey == null || "".equals(headerKey)) {
			headerKey = SessionConf.getInstance().getApplicationName() + SessionConstant.COOKIE_SESSION_ID_SUFFIX;
		}

		String sid = request.getHeader(headerKey);
		if (sid == null || "".equals(sid.trim())) {
			sid = UUID.randomUUID().toString().replace("-", "");
		}
		response.setHeader(headerKey, sid);
		return sid;
	}
}
