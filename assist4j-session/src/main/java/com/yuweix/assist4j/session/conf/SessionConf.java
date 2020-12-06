package com.yuweix.assist4j.session.conf;


import com.yuweix.assist4j.session.SessionConstant;
import com.yuweix.assist4j.session.cache.SessionCache;
import org.springframework.util.Assert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author yuwei
 */
public class SessionConf {
	private PathPattern exclusivePattern;
	/**
	 * session失效时间(分钟)
	 */
	private int maxInactiveInterval;
	private String applicationName;
	private SessionCache cache;

	private SessionConf() {

	}

	private static class Holder {
		private static final SessionConf instance = new SessionConf();
	}

	public static SessionConf getInstance() {
		return Holder.instance;
	}

	public void setExclusivePattern(String[] exclusiveURLs) {
		exclusivePattern = new PathPattern(exclusiveURLs);
	}

	public PathPattern getExclusivePattern() {
		return exclusivePattern;
	}

	public void setCache(SessionCache cache) {
		Assert.notNull(cache, "[cache] is required.");
		this.cache = cache;
	}

	public SessionCache getCache() {
		Assert.notNull(cache, "[cache] is required.");
		return cache;
	}

	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}

	public int getMaxInactiveInterval() {
		if (maxInactiveInterval <= 0) {
			return SessionConstant.DEFAULT_MAX_INACTIVE_INTERVAL;
		} else {
			return this.maxInactiveInterval;
		}
	}

	public void setApplicationName(String applicationName) {
		Assert.notNull(applicationName, "[applicationName] is required.");
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Matcher m = Pattern.compile(regEx).matcher(applicationName);
		this.applicationName = m.replaceAll("").trim();
	}

	public String getApplicationName() {
		Assert.notNull(applicationName, "[applicationName] is required.");
		return applicationName;
	}

	public void check() {
		Assert.notNull(cache, "[cache] is required.");
		Assert.notNull(applicationName, "[applicationName] is required.");
	}
}
