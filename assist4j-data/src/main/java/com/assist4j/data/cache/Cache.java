package com.assist4j.data.cache;


import com.alibaba.fastjson.TypeReference;


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
	 * @param value
	 * @param timeout 过期时间(s)。
	 * @return
	 */
	<T>boolean put(String key, T value, long timeout);

	String get(String key);
	/**
	 * @param key
	 * @param clz
	 * @param <T>
	 * @return
	 */
	<T>T get(String key, Class<T> clz);
	<T>T get(String key, TypeReference<T> type);

	<T>boolean putSplit(String key, T value, long timeout, int maxLength);
	String getSplit(String key);
	<T>T getSplit(String key, Class<T> clz);
	<T>T getSplit(String key, TypeReference<T> type);
	void removeSplit(String key);

	/**
	 * @param key
	 */
	void remove(String key);

	/**
	 * timeout 过期时间(s)。
	 * @return
	 */
	<T>boolean hset(String key, String field, T value, long timeout);
	String hget(String key, String field);
	<T>T hget(String key, String field, Class<T> clz);
	<T>T hget(String key, String field, TypeReference<T> type);
	void remove(String key, String field);

	<T>boolean lock(String key, T owner, long timeout);
	/**
	 * @param key
	 * @param owner
	 * @param timeout 单位：秒。
	 * @param reentrant   是否可重入
	 * @return
	 */
	<T>boolean lock(String key, T owner, long timeout, boolean reentrant);
	<T>boolean unlock(String key, T owner);
}
