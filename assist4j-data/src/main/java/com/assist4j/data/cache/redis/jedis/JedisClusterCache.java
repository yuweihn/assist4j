package com.assist4j.data.cache.redis.jedis;


import java.util.*;

import com.assist4j.data.cache.AbstractCache;
import com.assist4j.data.cache.MessageHandler;
import com.assist4j.data.cache.redis.RedisCache;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;


/**
 * @author yuwei
 */
public class JedisClusterCache extends AbstractCache implements RedisCache {
	protected JedisCluster jedisCluster;
	protected RedisSerializer<Object> redisSerializer;


	public JedisClusterCache() {

	}


	public void setJedisCluster(JedisCluster jedisCluster) {
		this.jedisCluster = jedisCluster;
	}

	public void setRedisSerializer(RedisSerializer<Object> redisSerializer) {
		this.redisSerializer = redisSerializer;
	}

	private String serialize(Object o) {
		if (o == null) {
			return null;
		}
		return new String(redisSerializer.serialize(o));
	}
	private <T>T deserialize(String str) {
		if (str == null) {
			return null;
		}
		return (T) redisSerializer.deserialize(str.getBytes());
	}

	@Override
	public void publish(String channel, String message) {
		jedisCluster.publish(channel, message);
	}

	@Override
	public void subscribe(final String channel, final MessageHandler handler) {
		new Thread() {
			@Override
			public void run() {
				jedisCluster.subscribe(new JedisPubSub() {
					@Override
					public void onMessage(String channel, String message) {
						handler.handle(channel, message);
					}
				}, channel);
			}
		}.start();
	}

	@Override
	public boolean contains(String key) {
		Boolean exists = jedisCluster.exists(key);
		return exists != null && exists;
	}

	@Override
	public void expire(String key, long timeout) {
		jedisCluster.expire(key, (int) timeout);
	}

	@Override
	public <T> boolean put(String key, T value, long timeout) {
		if (timeout <= 0) {
			throw new RuntimeException("Invalid parameter[timeout].");
		}

		String res = jedisCluster.setex(key, (int) timeout, serialize(value));
		return "OK".equalsIgnoreCase(res);
	}

	@Override
	public <T>T get(String key) {
		return (T) deserialize(jedisCluster.get(key));
	}

	@Override
	public void remove(String key) {
		jedisCluster.del(key);
	}

	@Override
	public <T>boolean hset(String key, String field, T value, long timeout) {
		jedisCluster.hset(key, field, serialize(value));
		jedisCluster.expire(key, (int) timeout);
		return true;
	}

	@Override
	public <T>boolean hmset(String key, Map<String, T> entries, long timeout) {
		if (entries == null || entries.isEmpty()) {
			return true;
		}
		Map<String, String> strMap = new HashMap<String, String>();
		for (Map.Entry<String, T> entry: entries.entrySet()) {
			strMap.put(entry.getKey(), serialize(entry.getValue()));
		}
		jedisCluster.hmset(key, strMap);
		jedisCluster.expire(key, (int) timeout);
		return true;
	}

	@Override
	public <T>T hget(String key, String field) {
		return (T) deserialize(jedisCluster.hget(key, field));
	}

	@Override
	public <T>Map<String, T> hgetAll(String key) {
		Map<String, String> strMap = jedisCluster.hgetAll(key);
		Map<String, T> resMap = new HashMap<String, T>();
		if (strMap == null || strMap.isEmpty()) {
			return resMap;
		}
		for (Map.Entry<String, String> strEntry: strMap.entrySet()) {
			resMap.put(strEntry.getKey(), (T) deserialize(strEntry.getValue()));
		}
		return resMap;
	}

	@Override
	public void remove(String key, String field) {
		jedisCluster.hdel(key, field);
	}

	@Override
	public <T>boolean lpush(String key, T value, long timeout) {
		jedisCluster.lpush(key, serialize(value));
		jedisCluster.expire(key, (int) timeout);
		return true;
	}

	@Override
	public <T>boolean lpush(String key, List<T> valList, long timeout) {
		if (valList == null || valList.size() <= 0) {
			return true;
		}
		List<String> strList = new ArrayList<String>();
		for (T t: valList) {
			strList.add(serialize(t));
		}
		jedisCluster.lpush(key, strList.toArray(new String[0]));
		jedisCluster.expire(key, (int) timeout);
		return true;
	}

	@Override
	public <T>boolean rpush(String key, T value, long timeout) {
		jedisCluster.rpush(key, serialize(value));
		jedisCluster.expire(key, (int) timeout);
		return true;
	}

	@Override
	public <T>boolean rpush(String key, List<T> valList, long timeout) {
		if (valList == null || valList.size() <= 0) {
			return true;
		}
		List<String> strList = new ArrayList<String>();
		for (T t: valList) {
			strList.add(serialize(t));
		}
		jedisCluster.rpush(key, strList.toArray(new String[0]));
		jedisCluster.expire(key, (int) timeout);
		return true;
	}

	@Override
	public long lsize(String key) {
		return jedisCluster.llen(key);
	}

	@Override
	public String lindex(String key, long index) {
		return jedisCluster.lindex(key, index);
	}

	@Override
	public <T>List<T> lrange(String key, long start, long end) {
		List<String> strList = jedisCluster.lrange(key, start, end);
		List<T> tList = new ArrayList<T>();
		if (strList == null || strList.size() <= 0) {
			return tList;
		}
		for (String str: strList) {
			tList.add((T) deserialize(str));
		}
		return tList;
	}

	@Override
	public void ltrim(String key, long start, long end) {
		jedisCluster.ltrim(key, start, end);
	}

	@Override
	public <T>void lset(String key, long index, T value) {
		jedisCluster.lset(key, index, serialize(value));
	}

	@Override
	public <T>T lpop(String key) {
		return deserialize(jedisCluster.lpop(key));
	}

	@Override
	public <T>T rpop(String key) {
		return deserialize(jedisCluster.rpop(key));
	}

	@Override
	public <T>void sadd(String key, T t, long timeout) {
		jedisCluster.sadd(key, serialize(t));
		jedisCluster.expire(key, (int) timeout);
	}

	@Override
	public <T>void sadd(String key, List<T> valList, long timeout) {
		if (valList == null || valList.size() <= 0) {
			return;
		}
		List<String> strList = new ArrayList<String>();
		for (T t: valList) {
			strList.add(serialize(t));
		}
		jedisCluster.sadd(key, strList.toArray(new String[0]));
		jedisCluster.expire(key, (int) timeout);
	}

	@Override
	public long slen(String key) {
		return jedisCluster.scard(key);
	}

	@Override
	public <T>Set<T> sdiff(String key1, String key2) {
		Set<String> strSet = jedisCluster.sdiff(key1, key2);
		Set<T> tSet = new HashSet<T>();
		if (strSet == null || strSet.isEmpty()) {
			return tSet;
		}
		for (String str: strSet) {
			tSet.add((T) deserialize(str));
		}
		return tSet;
	}

	private boolean setNx(String key, Object owner, long timeout) {
		String res = jedisCluster.set(key, serialize(owner), "NX", "EX", (int) timeout);
		return "OK".equalsIgnoreCase(res);
	}
	@SuppressWarnings("unused")
	private boolean setXx(String key, Object owner, long timeout) {
		String res = jedisCluster.set(key, serialize(owner), "XX", "EX", (int) timeout);
		return "OK".equalsIgnoreCase(res);
	}
	private boolean setXxEquals(String key, Object owner, long timeout) {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/getLockXxEquals.lua")));
		Object result = jedisCluster.eval(redisScript.getScriptAsString(), Collections.singletonList(key), Arrays.asList(serialize(owner), "" + timeout));
		return result != null && "OK".equalsIgnoreCase(result.toString());
	}

	@Override
	public <T>boolean lock(String key, T owner, long timeout) {
		return lock(key, owner, timeout, false);
	}

	@Override
	public <T>boolean lock(String key, T owner, long timeout, boolean reentrant) {
		return reentrant && setXxEquals(key, owner, timeout) || setNx(key, owner, timeout);
	}

	@Override
	public <T>boolean unlock(String key, T owner) {
		if (!contains(key)) {
			return true;
		}
		String val = serialize(owner);
		DefaultRedisScript<Long> redisScript = new DefaultRedisScript<Long>();
		redisScript.setResultType(Long.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/releaseLock.lua")));
		Object result = jedisCluster.eval(redisScript.getScriptAsString(), Collections.singletonList(key), Collections.singletonList(val));
		return result != null && "1".equals(result.toString());
	}

	@Override
	public String execute(String script, List<String> keyList, List<String> argList) {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptText(script);
		Object result = jedisCluster.eval(redisScript.getScriptAsString(), keyList, argList == null ? new ArrayList<String>() : argList);
		return result == null ? null : result.toString();
	}
}
