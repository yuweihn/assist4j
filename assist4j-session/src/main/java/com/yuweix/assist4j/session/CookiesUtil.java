package com.yuweix.assist4j.session;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Cookie操作工具
 * @author yuwei
 */
public abstract class CookiesUtil {
	/**
	 * 从Cookie中根据key取值
	 * @param key
	 * @return
	 */
	public static String findValueByKey(HttpServletRequest request, String key) {
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
	 * @param name Cookie名称 。
	 * @param value Cookie的值。
	 */
	public static void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value) {
		addCookie(request, response, name, value, null, null, SessionConstant.COOKIE_MAX_AGE_DEFAULT);
	}

	/**
	 * @param name cookie名称
	 * @param value cookie值
	 * @param domain cookie域名            默认：null
	 * @param contextPath cookie路径。     默认："/"
	 * @param maxAge 有效时间。
	 */
	public static void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value
			, String domain, String contextPath, int maxAge) {
		if (request == null || response == null) {
			throw new RuntimeException("Request and response can not be null.");
		}

		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(maxAge);
		cookie.setSecure(request.isSecure());

		if (contextPath == null || contextPath.isEmpty()) {
			contextPath = request.getContextPath();
		}
		if (contextPath == null || contextPath.isEmpty()) {
			contextPath = "/";
		}
		cookie.setPath(contextPath);

		if (domain != null && !domain.isEmpty()) {
			cookie.setDomain(domain);
		}

		response.addCookie(cookie);
	}

	/**
	 * 销毁Cookie
	 */
	public static void invalidate(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for(int i = 0; i < cookies.length; i++) {
				Cookie cookie = new Cookie(cookies[i].getName(), null);
				cookie.setValue("");
				cookie.setMaxAge(0);
				cookie.setPath("/");
				response.addCookie(cookie);
			}
		}
	}
}
