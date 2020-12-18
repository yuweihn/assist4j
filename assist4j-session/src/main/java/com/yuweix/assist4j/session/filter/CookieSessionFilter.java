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
	private String cookieName;


	public CookieSessionFilter(SessionCache cache) {
		super(cache);
	}
	public CookieSessionFilter() {

	}

	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}


	@Override
	protected String getSessionId(HttpServletRequest request, HttpServletResponse response) {
		String _cookieName = cookieName;
		if (_cookieName == null || "".equals(_cookieName)) {
			_cookieName = SessionConf.getInstance().getApplicationName() + SessionConstant.COOKIE_SESSION_ID_SUFFIX;
		}

		String sid = CookiesUtil.findValueByKey(request, _cookieName);
		if (sid == null || "".equals(sid)) {
			sid = UUID.randomUUID().toString().replace("-", "");
		}
		CookiesUtil.addCookie(request, response, _cookieName, sid,null,null, SessionConstant.COOKIE_MAX_AGE_DEFAULT);
		return sid;
	}
}
