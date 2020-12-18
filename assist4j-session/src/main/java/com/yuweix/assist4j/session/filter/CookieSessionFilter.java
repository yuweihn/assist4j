package com.yuweix.assist4j.session.filter;


import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.yuweix.assist4j.session.CookiesUtil;
import com.yuweix.assist4j.session.SessionConstant;
import com.yuweix.assist4j.session.cache.SessionCache;
import com.yuweix.assist4j.session.conf.SessionConf;


/**
 * @author yuwei
 */
public class CookieSessionFilter extends AbstractSessionFilter {
	public CookieSessionFilter(SessionCache cache) {
		super(cache);
	}
	public CookieSessionFilter() {

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
}
