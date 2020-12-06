package com.yuweix.assist4j.session.cache;





/**
 * @author yuwei
 */
public interface SessionCache {
	/**
	 * timeout 有效期(单位：秒)。
	 */
	boolean put(String key, String value, long timeout);
	String get(String key);
	void remove(String key);

	/**
	 * session同步完成之后的操作
	 */
	void afterCompletion(String sessionId);
}
