package com.assist4j.data.cache.redis.jedis;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.assist4j.data.cache.AbstractCache;
import com.assist4j.data.cache.MessageHandler;
import com.assist4j.data.cache.redis.RedisCache;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;


/**
 * @author yuwei
 */
public class JedisClusterCache extends AbstractCache implements RedisCache {
	protected JedisCluster jedisCluster;


	public JedisClusterCache() {

	}


	public void setJedisCluster(JedisCluster jedisCluster) {
		this.jedisCluster = jedisCluster;
	}


	@Override
	public void publish(String channel, String message) {
		jedisCluster.publish(channel, message);
	}

	@Override
	public void subscribe(String channel, final MessageHandler handler) {
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
	public <T> boolean put(String key, T value, long timeout) {
		if (timeout <= 0) {
			throw new RuntimeException("Invalid parameter[timeout].");
		}

		String res = null;
		if (value.getClass() == String.class) {
			res = jedisCluster.setex(key, (int) timeout, (String) value);
		} else {
			res = jedisCluster.setex(key, (int) timeout, JSON.toJSONString(value));
		}
		return "OK".equalsIgnoreCase(res);
	}

	@Override
	public String get(String key) {
		return jedisCluster.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key, Class<T> clz) {
		String val = get(key);
		if (val == null) {
			return null;
		}
		return clz == String.class ? (T) val : JSON.parseObject(val, clz);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key, TypeReference<T> type) {
		String val = get(key);
		if (val == null) {
			return null;
		}
		return type.getType() == String.class ? (T) val : JSON.parseObject(val, type);
	}

	@Override
	public void remove(String key) {
		jedisCluster.del(key);
	}

	private boolean setNx(String key, String owner, long timeout) {
		String res = jedisCluster.set(key, owner, "NX", "EX", (int) timeout);
		return "OK".equalsIgnoreCase(res);
	}
	@SuppressWarnings("unused")
	private boolean setXx(String key, String owner, long timeout) {
		String res = jedisCluster.set(key, owner, "XX", "EX", (int) timeout);
		return "OK".equalsIgnoreCase(res);
	}
	private boolean setXxEquals(String key, String owner, long timeout) {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/getLockXxEquals.lua")));
		Object result = jedisCluster.eval(redisScript.getScriptAsString(), Collections.singletonList(key), Arrays.asList(owner, "" + timeout));
		return result != null && "OK".equalsIgnoreCase(result.toString());
	}

	@Override
	public <T>boolean lock(String key, T owner, long timeout) {
		return lock(key, owner, timeout, false);
	}

	@Override
	public <T>boolean lock(String key, T owner, long timeout, boolean reentrant) {
		String val = null;
		if (owner.getClass() == String.class) {
			val = (String) owner;
		} else {
			val = JSON.toJSONString(owner);
		}
		return reentrant && setXxEquals(key, val, timeout) || setNx(key, val, timeout);
	}

	@Override
	public <T>boolean unlock(String key, T owner) {
		if (!contains(key)) {
			return true;
		}
		String val = null;
		if (owner.getClass() == String.class) {
			val = (String) owner;
		} else {
			val = JSON.toJSONString(owner);
		}
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
