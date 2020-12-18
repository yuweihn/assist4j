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
		String cookieName = SessionConf.getInstance().getApplicationName() + SessionConstant.COOKIE_SESSION_ID_SUFFIX;
		String sid = CookiesUtil.findValueByKey(request, cookieName);
		if (sid == null || "".equals(sid)) {
			sid = UUID.randomUUID().toString().replace("-", "");
		}
		CookiesUtil.addCookie(request, response, cookieName, sid,null,null, SessionConstant.COOKIE_MAX_AGE_DEFAULT);
		return sid;
	}
}
