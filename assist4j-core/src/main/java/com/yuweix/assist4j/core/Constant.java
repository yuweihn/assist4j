package com.yuweix.assist4j.core;


/**
 * @author yuwei
 **/
public abstract class Constant {
	public static final String ENCODING_UTF_8 = "utf-8";
	public static final String LOCALE_ZH_CN = "zh_CN";
	public static final String LOCALE_EN_US = "en_US";
	public static final String SESSION_USER_ID = "sessionUserId";
	public static final String SESSION_USER = "sessionUser";
	public static final String IMG_TYPE_JPG = ".jpg";
	public static final int DEFAULT_HTTP_PORT = 80;
	public static final int DEFAULT_HTTPS_PORT = 443;
	
	/**
	 * 每页默认显示条数
	 */
	public static final int DEFAULT_PAGE_SIZE = 10;
	/**
	 * 站点地址key
	 */
	public static final String CONTEXT_PATH_KEY = "contextPath";
	/**
	 * 静态资源地址key
	 */
	public static final String STATIC_PATH_KEY = "staticPath";
}
