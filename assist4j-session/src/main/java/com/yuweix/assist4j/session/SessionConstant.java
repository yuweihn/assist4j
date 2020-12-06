package com.yuweix.assist4j.session;





/**
 * @author yuwei
 */
public abstract class SessionConstant {
	/**
	 * 默认Cookie的生存期为关闭浏览器
	 */
	public static final int COOKIE_MAX_AGE_DEFAULT = -1;

	public static final String SESSION_ID_PRE = "cache.session.";
	public static final String SESSION_ID_KEY_CURRENT = "current";

	/**
	 * 默认session失效时间(分钟)
	 */
	public static final int DEFAULT_MAX_INACTIVE_INTERVAL = 30;

	public static final String COOKIE_SESSION_ID_SUFFIX = "SessionId";
}
