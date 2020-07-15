package com.assist4j.data.cache.redis;


import java.util.List;
import java.util.Map;
import java.util.Set;

import com.assist4j.data.cache.Cache;
import com.assist4j.data.cache.MessageHandler;


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
	<T>boolean hmset(String key, Map<String, T> entries, long timeout);
	<T>T hget(String key, String field);
	<T>Map<String, T> hgetAll(String key);
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
	<T>List<T> lrange(String key, long start, long end);
	void ltrim(String key, long start, long end);
	<T>void lset(String key, long index, T value);
	<T>T lpop(String key);
	<T>T rpop(String key);

	/**
	 * timeout 过期时间(s)。
	 */
	<T>void sadd(String key, T value, long timeout);
	<T>void sadd(String key, List<T> valList, long timeout);
	long slen(String key);
	<T>Set<T> sdiff(String key1, String key2);

	String execute(String script, List<String> keyList, List<String> argList);
}
