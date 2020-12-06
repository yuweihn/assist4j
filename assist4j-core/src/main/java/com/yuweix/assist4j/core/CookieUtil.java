package com.yuweix.assist4j.core;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Cookie操作工具
 * @author yuwei
 */
public abstract class CookieUtil {
	public static final int COOKIE_MAX_AGE_1W = 7 * 24 * 60 * 60;// 1 week
	public static final int COOKIE_MAX_AGE_DEFAULT = -1;//默认Cookie的生存期为关闭浏览器


	
	/**
	 * 从Cookie中根据key取值
	 * @param key
	 * @return
	 */
	public static String findValueByKey(String key) {
		HttpServletRequest request = ActionUtil.getRequest();
		if (request == null) {
			return null;
		}

		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length <= 0) {
			return null;
		}

		for (Cookie cookie: cookies) {
			if (cookie.getName().equals(key)) {
				return cookie.getValue();
			}
		}

		return null;
	}

	/**
	 * 增加一个Cookie,使用默认域名。
	 * @param name Cookie名称 。
	 * @param value Cookie的值。
	 */
	public static void addCookie(String name, String value) {
		addCookie(name, value, null, COOKIE_MAX_AGE_DEFAULT);
	}


	/**
	 * 增加一个Cookie,使用默认域名。
	 * @param name Cookie名称 。
	 * @param value Cookie的值。
	 * @param maxAge 生命周期。
	 */
	public static void addCookie(String name, String value, int maxAge) {
		addCookie(name, value, null, maxAge);
	}

	/**
	 * 增加一个Cookie,使用指定域名。
	 * @param name Cookie名称 。
	 * @param value Cookie的值。
	 * @param domain cookie域名
	 * @param maxAge 生命周期。
	 */
	public static void addCookie(String name, String value, String domain, int maxAge) {
		HttpServletRequest request = ActionUtil.getRequest();
		String contextPath = request.getContextPath();
		if (contextPath == null || contextPath.isEmpty()) {
			contextPath = "/";
		}
		addCookie(name, value, domain, contextPath, maxAge);
	}

	/**
	 * 增加一个Cookie.ContextPath如果为空或者长度为0，将使用"/".
	 * @param name cookie名称
	 * @param value cookie值
	 * @param domain cookie域名
	 * @param contextPath cookie路径。
	 * @param maxAge 有效时间。
	 */
	public static void addCookie(String name, String value, String domain, String contextPath, int maxAge) {
		HttpServletRequest request = ActionUtil.getRequest();
		HttpServletResponse response = ActionUtil.getResponse();
		if (request == null || response == null) {
			throw new RuntimeException("Request and response are required.");
		}

		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(maxAge);
		cookie.setSecure(request.isSecure());

		if (contextPath == null || contextPath.isEmpty()) {
			cookie.setPath("/");
		} else {
			cookie.setPath(contextPath);
		}

		if (domain != null && !domain.isEmpty()) {
			cookie.setDomain(domain);
		}

		response.addCookie(cookie);
	}

	/**
	 * 销毁Cookie
	 */
	public static void invalidate() {
		Cookie[] cookies = ActionUtil.getRequest().getCookies();
		if (cookies != null) {
			for(int i = 0; i < cookies.length; i++) {
				Cookie cookie = new Cookie(cookies[i].getName(), null);
				cookie.setValue("");
				cookie.setMaxAge(0);
				cookie.setPath("/");
				ActionUtil.getResponse().addCookie(cookie);
			}
		}
	}
}
