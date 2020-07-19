package com.assist4j.data.cache.redis;


import java.util.Collection;
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
	<T>T lindex(String key, long index);
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
	<T>Set<T> sdiff(String key, Collection<String> otherKeys);
	void sdiffStore(String key, Collection<String> otherKeys, String destKey);
	<T>Set<T> sinter(String key, Collection<String> otherKeys);
	void sinterStore(String key, Collection<String> otherKeys, String destKey);
	<T>Set<T> sunion(String key, Collection<String> otherKeys);
	void sunionStore(String key, Collection<String> otherKeys, String destKey);
	<T>boolean sisMember(String key, T member);
	<T>Set<T> smembers(String key);
	<T>boolean smove(String sourceKey, String destKey, T member);
	<T>boolean sremove(String key, Collection<T> members);

	/**
	 * timeout 过期时间(s)。
	 */
	<T>void zadd(String key, T value, double score, long timeout);
	<T>void zadd(String key, Map<T, Double> memScore, long timeout);
	long zlen(String key);
	long zcount(String key, double min, double max);
	<T>void zincrby(String key, T member, double increment);
	void zinterStore(String key, Collection<String> otherKeys, String destKey);
	void zunionStore(String key, Collection<String> otherKeys, String destKey);
	<T>boolean zremove(String key, Collection<T> members);
	<T>Double zscore(String key, T member);
	<T>Long zrank(String key, T member);

	String execute(String script, List<String> keyList, List<String> argList);
}
