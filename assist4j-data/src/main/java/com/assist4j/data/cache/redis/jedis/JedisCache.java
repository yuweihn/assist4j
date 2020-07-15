package com.assist4j.data.cache.redis.jedis;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
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

		if (value.getClass() == String.class) {
			redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
		} else {
			redisTemplate.opsForValue().set(key, JSON.toJSONString(value), timeout, TimeUnit.SECONDS);
		}
		return true;
	}

	@Override
	public String get(String key) {
		return (String) redisTemplate.opsForValue().get(key);
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
		redisTemplate.delete(key);
	}

	@Override
	public <T>boolean hset(String key, String field, T value, long timeout) {
		String val = null;
		if (value.getClass() != String.class) {
			val = JSON.toJSONString(value);
		} else {
			val = (String) value;
		}
		redisTemplate.opsForHash().put(key, field, val);
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		return true;
	}

	@Override
	public String hget(String key, String field) {
		return (String) redisTemplate.opsForHash().get(key, field);
	}

	@Override
	public <T>T hget(String key, String field, Class<T> clz) {
		String val = hget(key, field);
		if (val == null) {
			return null;
		}
		return clz == String.class ? (T) val : JSON.parseObject(val, clz);
	}

	@Override
	public <T>T hget(String key, String field, TypeReference<T> type) {
		String val = hget(key, field);
		if (val == null) {
			return null;
		}
		return type.getType() == String.class ? (T) val : JSON.parseObject(val, type);
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		Map<?, ?> entries = redisTemplate.opsForHash().entries(key);
		return (Map<String, String>) entries;
	}

	@Override
	public <T>Map<String, T> hgetAll(String key, Class<T> clz) {
		Map<String, String> strMap = hgetAll(key);
		if (clz == String.class) {
			return (Map<String, T>) strMap;
		}
		Map<String, T> resultMap = new HashMap<String, T>();
		if (strMap == null || strMap.isEmpty()) {
			return resultMap;
		}
		for (Map.Entry<String, String> entry: strMap.entrySet()) {
			resultMap.put(entry.getKey(), JSON.parseObject(entry.getValue(), clz));
		}
		return resultMap;
	}

	@Override
	public <T>Map<String, T> hgetAll(String key, TypeReference<T> type) {
		Map<String, String> strMap = hgetAll(key);
		if (type.getType() == String.class) {
			return (Map<String, T>) strMap;
		}
		Map<String, T> resultMap = new HashMap<String, T>();
		if (strMap == null || strMap.isEmpty()) {
			return resultMap;
		}
		for (Map.Entry<String, String> entry: strMap.entrySet()) {
			resultMap.put(entry.getKey(), JSON.parseObject(entry.getValue(), type));
		}
		return resultMap;
	}

	@Override
	public <T>boolean hmset(String key, Map<String, T> entries, long timeout) {
		if (entries == null || entries.isEmpty()) {
			return true;
		}
		Class<?> valClass = entries.values().toArray()[0].getClass();
		if (valClass == String.class) {
			redisTemplate.opsForHash().putAll(key, (Map<String, String>) entries);
		} else {
			Map<String, String> strMap = new HashMap<String, String>();
			for (Map.Entry<String, T> entry: entries.entrySet()) {
				strMap.put(entry.getKey(), JSON.toJSONString(entry.getValue()));
			}
			redisTemplate.opsForHash().putAll(key, strMap);
		}
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		return true;
	}

	@Override
	public void remove(String key, String field) {
		redisTemplate.opsForHash().delete(key, field);
	}

	@Override
	public <T>boolean lpush(String key, T value, long timeout) {
		String val = null;
		if (value.getClass() != String.class) {
			val = JSON.toJSONString(value);
		} else {
			val = (String) value;
		}
		redisTemplate.opsForList().leftPush(key, value);
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		return true;
	}

	@Override
	public <T>boolean lpush(String key, List<T> valList, long timeout) {
		if (valList == null || valList.size() <= 0) {
			return true;
		}
		List<String> strList = new ArrayList<String>();
		for (T t: valList) {
			strList.add(JSON.toJSONString(t));
		}
		redisTemplate.opsForList().leftPushAll(key, strList);
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		return true;
	}

	@Override
	public <T>boolean rpush(String key, T value, long timeout) {
		String val = null;
		if (value.getClass() != String.class) {
			val = JSON.toJSONString(value);
		} else {
			val = (String) value;
		}
		redisTemplate.opsForList().rightPush(key, value);
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		return true;
	}

	@Override
	public <T>boolean rpush(String key, List<T> valList, long timeout) {
		if (valList == null || valList.size() <= 0) {
			return true;
		}
		List<String> strList = new ArrayList<String>();
		for (T t: valList) {
			strList.add(JSON.toJSONString(t));
		}
		redisTemplate.opsForList().rightPushAll(key, strList);
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
	public <T>T lindex(String key, long index, Class<T> clz) {
		String val = lindex(key, index);
		if (val == null) {
			return null;
		}
		return clz == String.class ? (T) val : JSON.parseObject(val, clz);
	}

	@Override
	public <T>T lindex(String key, long index, TypeReference<T> type) {
		String val = lindex(key, index);
		if (val == null) {
			return null;
		}
		return type.getType() == String.class ? (T) val : JSON.parseObject(val, type);
	}

	@Override
	public List<String> lrange(String key, long start, long end) {
		List<?> list = redisTemplate.opsForList().range(key, start, end);
		return (List<String>) list;
	}

	@Override
	public <T>List<T> lrange(String key, long start, long end, Class<T> clz) {
		List<String> strList = lrange(key, start, end);
		if (clz == String.class) {
			return (List<T>) strList;
		}
		List<T> resultList = new ArrayList<T>();
		if (strList == null || strList.isEmpty()) {
			return resultList;
		}
		for (String str: strList) {
			resultList.add(JSON.parseObject(str, clz));
		}
		return resultList;
	}

	@Override
	public <T>List<T> lrange(String key, long start, long end, TypeReference<T> type) {
		List<String> strList = lrange(key, start, end);
		if (type.getType() == String.class) {
			return (List<T>) strList;
		}
		List<T> resultList = new ArrayList<T>();
		if (strList == null || strList.isEmpty()) {
			return resultList;
		}
		for (String str: strList) {
			resultList.add(JSON.parseObject(str, type));
		}
		return resultList;
	}

	@Override
	public void ltrim(String key, long start, long end) {
		redisTemplate.opsForList().trim(key, start, end);
	}

	@Override
	public <T>void lset(String key, long index, T value) {
		String val = null;
		if (value.getClass() != String.class) {
			val = JSON.toJSONString(value);
		} else {
			val = (String) value;
		}
		redisTemplate.opsForList().set(key, index, val);
	}

	@Override
	public String lpop(String key) {
		return (String) redisTemplate.opsForList().leftPop(key);
	}

	@Override
	public <T>T lpop(String key, Class<T> clz) {
		String val = lpop(key);
		if (val == null) {
			return null;
		}
		return clz == String.class ? (T) val : JSON.parseObject(val, clz);
	}

	@Override
	public <T>T lpop(String key, TypeReference<T> type) {
		String val = lpop(key);
		if (val == null) {
			return null;
		}
		return type.getType() == String.class ? (T) val : JSON.parseObject(val, type);
	}

	@Override
	public String rpop(String key) {
		return (String) redisTemplate.opsForList().rightPop(key);
	}

	@Override
	public <T>T rpop(String key, Class<T> clz) {
		String val = rpop(key);
		if (val == null) {
			return null;
		}
		return clz == String.class ? (T) val : JSON.parseObject(val, clz);
	}

	@Override
	public <T>T rpop(String key, TypeReference<T> type) {
		String val = rpop(key);
		if (val == null) {
			return null;
		}
		return type.getType() == String.class ? (T) val : JSON.parseObject(val, type);
	}

	private boolean setNx(String key, String owner, long timeout) {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/getLockNx.lua")));
		String result = redisTemplate.execute(redisScript, Collections.singletonList(key), owner, "" + timeout);
		return result != null && "OK".equalsIgnoreCase(result);
	}
	@SuppressWarnings("unused")
	private boolean setXx(String key, String owner, long timeout) {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/getLockXx.lua")));
		String result = redisTemplate.execute(redisScript, Collections.singletonList(key), owner, "" + timeout);
		return result != null && "OK".equalsIgnoreCase(result);
	}
	private boolean setXxEquals(String key, String owner, long timeout) {
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
		Long result = redisTemplate.execute(redisScript, Collections.singletonList(key), val);
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
