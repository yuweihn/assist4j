package com.assist4j.data.cache.redis.jedis;


import com.assist4j.data.cache.AbstractCache;
import com.assist4j.data.cache.MessageHandler;
import com.assist4j.data.cache.redis.RedisCache;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @author yuwei
 */
public class JedisCache extends AbstractCache implements RedisCache {
	protected RedisTemplate<String, Object> redisTemplate;


	public JedisCache() {

	}


	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}


	@Override
	public void publish(final String channel, final String message) {
		redisTemplate.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				connection.publish(channel.getBytes(StandardCharsets.UTF_8), message.getBytes(StandardCharsets.UTF_8));
				return null;
			}
		});
	}

	@Override
	public void subscribe(final String channel, final MessageHandler handler) {
		redisTemplate.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				connection.subscribe(new MessageListener() {
					@Override
					public void onMessage(Message message, byte[] bytes) {
						String channel = null;
						String msg = null;

						if (message.getChannel() != null) {
							channel = new String(message.getChannel(), StandardCharsets.UTF_8);
						}

						if (message.getBody() != null) {
							msg = new String(message.getBody(), StandardCharsets.UTF_8);
						}

						handler.handle(channel, msg);
					}
				}, channel.getBytes(StandardCharsets.UTF_8));
				return null;
			}
		});
	}

	@Override
	public boolean contains(String key) {
		Object object = redisTemplate.opsForValue().get(key);
		return object != null;
	}

	@Override
	public void expire(String key, long timeout) {
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
	}

	@Override
	public <T> boolean put(String key, T value, long timeout) {
		if (timeout <= 0) {
			throw new RuntimeException("Invalid parameter[timeout].");
		}

		redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
		return true;
	}

	@Override
	public <T>T get(String key) {
		return (T) redisTemplate.opsForValue().get(key);
	}

	@Override
	public void remove(String key) {
		redisTemplate.delete(key);
	}

	@Override
	public <T>boolean hset(String key, String field, T value, long timeout) {
		redisTemplate.opsForHash().put(key, field, value);
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		return true;
	}

	@Override
	public <T>boolean hmset(String key, Map<String, T> entries, long timeout) {
		if (entries == null || entries.isEmpty()) {
			return true;
		}
		redisTemplate.opsForHash().putAll(key, entries);
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		return true;
	}

	@Override
	public <T>T hget(String key, String field) {
		return (T) redisTemplate.opsForHash().get(key, field);
	}

	@Override
	public <T>Map<String, T> hgetAll(String key) {
		Map<?, ?> entries = redisTemplate.opsForHash().entries(key);
		return (Map<String, T>) entries;
	}

	@Override
	public void remove(String key, String field) {
		redisTemplate.opsForHash().delete(key, field);
	}

	@Override
	public <T>boolean lpush(String key, T value, long timeout) {
		redisTemplate.opsForList().leftPush(key, value);
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		return true;
	}

	@Override
	public <T>boolean lpush(String key, List<T> valList, long timeout) {
		if (valList == null || valList.size() <= 0) {
			return true;
		}
		redisTemplate.opsForList().leftPushAll(key, valList);
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		return true;
	}

	@Override
	public <T>boolean rpush(String key, T value, long timeout) {
		redisTemplate.opsForList().rightPush(key, value);
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		return true;
	}

	@Override
	public <T>boolean rpush(String key, List<T> valList, long timeout) {
		if (valList == null || valList.size() <= 0) {
			return true;
		}
		redisTemplate.opsForList().rightPushAll(key, valList);
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		return true;
	}

	@Override
	public long lsize(String key) {
		return redisTemplate.opsForList().size(key);
	}

	@Override
	public String lindex(String key, long index) {
		return (String) redisTemplate.opsForList().index(key, index);
	}

	@Override
	public <T>List<T> lrange(String key, long start, long end) {
		return (List<T>) redisTemplate.opsForList().range(key, start, end);
	}

	@Override
	public void ltrim(String key, long start, long end) {
		redisTemplate.opsForList().trim(key, start, end);
	}

	@Override
	public <T>void lset(String key, long index, T value) {
		redisTemplate.opsForList().set(key, index, value);
	}

	@Override
	public <T>T lpop(String key) {
		return (T) redisTemplate.opsForList().leftPop(key);
	}

	@Override
	public <T>T rpop(String key) {
		return (T) redisTemplate.opsForList().rightPop(key);
	}

	@Override
	public <T>void sadd(String key, T t, long timeout) {
		redisTemplate.opsForSet().add(key, t);
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
	}

	@Override
	public <T>void sadd(String key, List<T> valList, long timeout) {
		if (valList == null || valList.size() <= 0) {
			return;
		}
		redisTemplate.opsForSet().add(key, valList.toArray());
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
	}

	@Override
	public long slen(String key) {
		return redisTemplate.opsForSet().size(key);
	}

	@Override
	public <T>Set<T> sdiff(String key1, String key2) {
		Set<?> set = redisTemplate.opsForSet().difference(key1, key2);
		return (Set<T>) set;
	}

	private <T>boolean setNx(String key, T owner, long timeout) {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/getLockNx.lua")));
		String result = redisTemplate.execute(redisScript, Collections.singletonList(key), owner, "" + timeout);
		return result != null && "OK".equalsIgnoreCase(result);
	}
	@SuppressWarnings("unused")
	private <T>boolean setXx(String key, T owner, long timeout) {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/getLockXx.lua")));
		String result = redisTemplate.execute(redisScript, Collections.singletonList(key), owner, "" + timeout);
		return result != null && "OK".equalsIgnoreCase(result);
	}
	private <T>boolean setXxEquals(String key, T owner, long timeout) {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/getLockXxEquals.lua")));
		String result = redisTemplate.execute(redisScript, Collections.singletonList(key), owner, "" + timeout);
		return result != null && "OK".equalsIgnoreCase(result);
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
		DefaultRedisScript<Long> redisScript = new DefaultRedisScript<Long>();
		redisScript.setResultType(Long.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/releaseLock.lua")));
		Long result = redisTemplate.execute(redisScript, Collections.singletonList(key), owner);
		return result != null && "1".equals(result.toString());
	}

	@Override
	public String execute(String script, List<String> keyList, List<String> argList) {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptText(script);
		return redisTemplate.execute(redisScript, keyList, argList == null ? new Object[0] : argList.toArray());
	}
}
