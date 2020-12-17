package com.yuweix.assist4j.session;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import com.yuweix.assist4j.session.conf.SessionConf;


/**
 * HttpServletRequest的包装器，修改了获取Session方法的行为。
 * 现在将返回一个实际型别是{@link com.yuweix.assist4j.session.CacheHttpSession}的HttpSession实例。
 *
 * 判断Session是否为失效超过最大不活动时间的时机是每次获取Session时。
 * 比如getSession()和getSession(true)都将造成更新最后访问时间。
 * 但是getSession(false)被调用时，如果本身没有绑定Session那么不会有这个动作。
 *
 * @author yuwei
 */
public class CacheHttpServletRequest extends HttpServletRequestWrapper {
	private CacheHttpSession cacheSession;
	private String sessionId;


	/**
	 * 构造一个HttpServletRequest的包装器。
	 */
	public CacheHttpServletRequest(HttpServletRequest request, String sessionId) {
		super(request);
		if (sessionId == null || "".equals(sessionId.trim())) {
			throw new RuntimeException("[sessionId]不能为空！！！");
		}
		this.sessionId = sessionId.trim();
	}


	/**
	 * 获取会话实例，如果不存在则创建。
	 * @return 会话实例。
	 */
	@Override
	public HttpSession getSession() {
		return getSession(true);
	}

	/**
	 * 获取会话实例，调用者指定如果不存在时是否创建一个新的。
	 * @param create true不存在则创建，false不存在返回null.
	 * @return 会话实例。
	 */
	@Override
	public HttpSession getSession(boolean create) {
		return doGetSession(create);
	}

	/**
	 * 如果Session不为空，同步至缓存。
	 */
	public void sync() {
		if (cacheSession == null) {
			return;
		}
		String fullSessionId = cacheSession.sync();
		if (fullSessionId == null || "".equals(fullSessionId)) {
			return;
		}
		SessionConf.getInstance().getCache().afterCompletion(fullSessionId);
	}

	/**
	 * @param create true创建，false不创建。
	 */
	private HttpSession doGetSession(boolean create) {
		if (cacheSession == null) {
			if (create) {
				cacheSession = new CacheHttpSession(sessionId);
			}
			return cacheSession;
		}

		if (cacheSession.isInvalid()) {
			cacheSession.removeSessionFromCache();
			if (create) {
				cacheSession = new CacheHttpSession(sessionId);
			}
		}
		cacheSession.access();
		return cacheSession;
	}
}
