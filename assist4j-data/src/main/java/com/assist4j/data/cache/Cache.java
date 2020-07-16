package com.assist4j.data.cache;


/**
 * @author yuwei
 */
public interface Cache {
	/**
	 * @param key
	 * @return
	 */
	boolean contains(String key);

	/**
	 * @param key
	 * @param timeout 过期时间(s)。
	 */
	void expire(String key, long timeout);

	/**
	 * @param key
	 * @param value
	 * @param timeout 过期时间(s)。
	 * @return
	 */
	<T>boolean put(String key, T value, long timeout);
	<T>T get(String key);

	boolean putSplit(String key, String value, long timeout, int maxLength);
	String getSplit(String key);
	void removeSplit(String key);

	/**
	 * @param key
	 */
	void remove(String key);

	boolean lock(String key, String owner, long timeout);
	/**
	 * @param key
	 * @param owner
	 * @param timeout 单位：秒。
	 * @param reentrant   是否可重入
	 * @return
	 */
	boolean lock(String key, String owner, long timeout, boolean reentrant);
	boolean unlock(String key, String owner);
}
