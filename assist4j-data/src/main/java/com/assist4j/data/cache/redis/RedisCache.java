package com.assist4j.data.cache.redis;


import com.alibaba.fastjson.TypeReference;
import com.assist4j.data.cache.Cache;
import com.assist4j.data.cache.MessageHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author yuwei
 */
public interface RedisCache extends Cache {
	/**
	 * 发布消息
	 * @param channel
	 * @param message
	 */
	void publish(String channel, String message);

	/**
	 * 订阅消息
	 * @param channel
	 * @param handler
	 */
	void subscribe(String channel, MessageHandler handler);

	/**
	 * timeout 过期时间(s)。
	 * @return
	 */
	<T>boolean hset(String key, String field, T value, long timeout);
	String hget(String key, String field);
	<T>T hget(String key, String field, Class<T> clz);
	<T>T hget(String key, String field, TypeReference<T> type);
	Map<String, String> hgetAll(String key);
	<T>Map<String, T> hgetAll(String key, Class<T> clz);
	<T>Map<String, T> hgetAll(String key, TypeReference<T> type);
	/**
	 * timeout 过期时间(s)。
	 * @return
	 */
	<T>boolean hmset(String key, Map<String, T> entries, long timeout);
	void remove(String key, String field);

	/**
	 * timeout 过期时间(s)。
	 * @return
	 */
	<T>boolean lpush(String key, T value, long timeout);
	<T>boolean lpush(String key, List<T> valList, long timeout);
	<T>boolean rpush(String key, T value, long timeout);
	<T>boolean rpush(String key, List<T> valList, long timeout);
	long lsize(String key);
	String lindex(String key, long index);
	<T>T lindex(String key, long index, Class<T> clz);
	<T>T lindex(String key, long index, TypeReference<T> type);
	List<String> lrange(String key, long start, long end);
	<T>List<T> lrange(String key, long start, long end, Class<T> clz);
	<T>List<T> lrange(String key, long start, long end, TypeReference<T> type);
	void ltrim(String key, long start, long end);
	<T>void lset(String key, long index, T value);
	String lpop(String key);
	<T>T lpop(String key, Class<T> clz);
	<T>T lpop(String key, TypeReference<T> type);
	String rpop(String key);
	<T>T rpop(String key, Class<T> clz);
	<T>T rpop(String key, TypeReference<T> type);

	/**
	 * timeout 过期时间(s)。
	 */
	<T>void sadd(String key, T... members);
	long ssize(String key);
	Set<String> sdiff(String key1, String key2);

	String execute(String script, List<String> keyList, List<String> argList);
}
