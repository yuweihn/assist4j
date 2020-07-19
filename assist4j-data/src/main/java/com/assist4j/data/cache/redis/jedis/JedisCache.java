package com.assist4j.data.cache.redis.jedis;


import com.assist4j.data.cache.AbstractCache;
import com.assist4j.data.cache.MessageHandler;
import com.assist4j.data.cache.redis.RedisCache;
import com.assist4j.data.serializer.DefaultSerializier;
import com.assist4j.data.serializer.Serializier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
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
	protected Serializier serializier;


	public JedisCache() {
		serializier = new DefaultSerializier();
	}


	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void setSerializier(Serializier serializier) {
		this.serializier = serializier;
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

		redisTemplate.opsForValue().set(key, serializier.serialize(value), timeout, TimeUnit.SECONDS);
		return true;
	}

	@Override
	public <T>T get(String key) {
		return (T) serializier.deserialize((String) redisTemplate.opsForValue().get(key));
	}

	@Override
	public void remove(String key) {
		redisTemplate.delete(key);
	}

	@Override
	public <T>boolean hset(String key, String field, T value, long timeout) {
		redisTemplate.opsForHash().put(key, field, serializier.serialize(value));
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		return true;
	}

	@Override
	public <T>boolean hmset(String key, Map<String, T> entries, long timeout) {
		if (entries == null || entries.isEmpty()) {
			return true;
		}
		Map<String, String> strMap = new HashMap<String, String>();
		for (Map.Entry<String, T> entry: entries.entrySet()) {
			strMap.put(entry.getKey(), serializier.serialize(entry.getValue()));
		}
		redisTemplate.opsForHash().putAll(key, strMap);
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		return true;
	}

	@Override
	public <T>T hget(String key, String field) {
		return (T) serializier.deserialize((String) redisTemplate.opsForHash().get(key, field));
	}

	@Override
	public <T>Map<String, T> hgetAll(String key) {
		Map<?, ?> entries = redisTemplate.opsForHash().entries(key);
		Map<String, String> strMap = (Map<String, String>) entries;
		Map<String, T> resMap = new HashMap<String, T>();
		if (strMap == null || strMap.isEmpty()) {
			return resMap;
		}
		for (Map.Entry<String, String> strEntry: strMap.entrySet()) {
			resMap.put(strEntry.getKey(), (T) serializier.deserialize(strEntry.getValue()));
		}
		return resMap;
	}

	@Override
	public void remove(String key, String field) {
		redisTemplate.opsForHash().delete(key, field);
	}

	@Override
	public <T>boolean lpush(String key, T value, long timeout) {
		redisTemplate.opsForList().leftPush(key, serializier.serialize(value));
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
			strList.add(serializier.serialize(t));
		}
		redisTemplate.opsForList().leftPushAll(key, strList.toArray(new String[0]));
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		return true;
	}

	@Override
	public <T>boolean rpush(String key, T value, long timeout) {
		redisTemplate.opsForList().rightPush(key, serializier.serialize(value));
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
			strList.add(serializier.serialize(t));
		}
		redisTemplate.opsForList().rightPushAll(key, strList.toArray(new String[0]));
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		return true;
	}

	@Override
	public long lsize(String key) {
		return redisTemplate.opsForList().size(key);
	}

	@Override
	public <T>T lindex(String key, long index) {
		return serializier.deserialize((String) redisTemplate.opsForList().index(key, index));
	}

	@Override
	public <T>List<T> lrange(String key, long start, long end) {
		List<?> strList = redisTemplate.opsForList().range(key, start, end);
		List<T> tList = new ArrayList<T>();
		if (strList == null || strList.size() <= 0) {
			return tList;
		}
		for (Object str: strList) {
			tList.add((T) serializier.deserialize((String) str));
		}
		return tList;
	}

	@Override
	public void ltrim(String key, long start, long end) {
		redisTemplate.opsForList().trim(key, start, end);
	}

	@Override
	public <T>void lset(String key, long index, T value) {
		redisTemplate.opsForList().set(key, index, serializier.serialize(value));
	}

	@Override
	public <T>T lpop(String key) {
		return serializier.deserialize((String) redisTemplate.opsForList().leftPop(key));
	}

	@Override
	public <T>T rpop(String key) {
		return serializier.deserialize((String) redisTemplate.opsForList().rightPop(key));
	}

	@Override
	public <T>void sadd(String key, T t, long timeout) {
		redisTemplate.opsForSet().add(key, serializier.serialize(t));
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
	}

	@Override
	public <T>void sadd(String key, List<T> valList, long timeout) {
		if (valList == null || valList.size() <= 0) {
			return;
		}
		List<String> strList = new ArrayList<String>();
		for (T t: valList) {
			strList.add(serializier.serialize(t));
		}
		redisTemplate.opsForSet().add(key, strList.toArray(new String[0]));
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
	}

	@Override
	public long slen(String key) {
		return redisTemplate.opsForSet().size(key);
	}

	@Override
	public <T>Set<T> sdiff(String key, Collection<String> otherKeys) {
		Set<Object> strSet = redisTemplate.opsForSet().difference(key, otherKeys);
		Set<T> tSet = new HashSet<T>();
		if (strSet == null || strSet.isEmpty()) {
			return tSet;
		}
		for (Object str: strSet) {
			tSet.add((T) serializier.deserialize((String) str));
		}
		return tSet;
	}

	@Override
	public void sdiffStore(String key, Collection<String> otherKeys, String destKey) {
		redisTemplate.opsForSet().differenceAndStore(key, otherKeys, destKey);
	}

	@Override
	public <T>Set<T> sinter(String key, Collection<String> otherKeys) {
		Set<Object> strSet = redisTemplate.opsForSet().intersect(key, otherKeys);
		Set<T> tSet = new HashSet<T>();
		if (strSet == null || strSet.isEmpty()) {
			return tSet;
		}
		for (Object str: strSet) {
			tSet.add((T) serializier.deserialize((String) str));
		}
		return tSet;
	}

	@Override
	public void sinterStore(String key, Collection<String> otherKeys, String destKey) {
		redisTemplate.opsForSet().intersectAndStore(key, otherKeys, destKey);
	}

	@Override
	public <T>Set<T> sunion(String key, Collection<String> otherKeys) {
		Set<Object> strSet = redisTemplate.opsForSet().union(key, otherKeys);
		Set<T> tSet = new HashSet<T>();
		if (strSet == null || strSet.isEmpty()) {
			return tSet;
		}
		for (Object str: strSet) {
			tSet.add((T) serializier.deserialize((String) str));
		}
		return tSet;
	}

	@Override
	public void sunionStore(String key, Collection<String> otherKeys, String destKey) {
		redisTemplate.opsForSet().unionAndStore(key, otherKeys, destKey);
	}

	@Override
	public <T>boolean sisMember(String key, T member) {
		return redisTemplate.opsForSet().isMember(key, serializier.serialize(member));
	}

	@Override
	public <T>Set<T> smembers(String key) {
		Set<Object> strSet = redisTemplate.opsForSet().members(key);
		Set<T> tSet = new HashSet<T>();
		if (strSet == null || strSet.isEmpty()) {
			return tSet;
		}
		for (Object str: strSet) {
			tSet.add((T) serializier.deserialize((String) str));
		}
		return tSet;
	}

	@Override
	public <T>boolean smove(String sourceKey, String destKey, T member) {
		return redisTemplate.opsForSet().move(sourceKey, serializier.serialize(member), destKey);
	}

	@Override
	public <T>boolean sremove(String key, Collection<T> members) {
		if (members == null || members.size() <= 0) {
			return false;
		}
		List<String> strList = new ArrayList<String>();
		for (T t: members) {
			strList.add(serializier.serialize(t));
		}
		return redisTemplate.opsForSet().remove(key, strList.toArray(new String[0])) > 0;
	}

	@Override
	public <T>void zadd(String key, T value, double score, long timeout) {
		redisTemplate.opsForZSet().add(key, serializier.serialize(value), score);
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
	}

	@Override
	public <T>void zadd(String key, Map<T, Double> memScore, long timeout) {
		Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<ZSetOperations.TypedTuple<Object>>();
		for (final Map.Entry<T, Double> entry: memScore.entrySet()) {
			tuples.add(new ZSetOperations.TypedTuple<Object>() {
				@Override
				public int compareTo(ZSetOperations.TypedTuple<Object> o) {
					return this.getScore() > o.getScore() ? 1 : -1;
				}

				@Override
				public Object getValue() {
					return serializier.serialize(entry.getKey());
				}

				@Override
				public Double getScore() {
					return entry.getValue();
				}
			});
		}
		redisTemplate.opsForZSet().add(key, tuples);
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
	}

	@Override
	public long zlen(String key) {
		return redisTemplate.opsForZSet().size(key);
	}

	@Override
	public long zcount(String key, double min, double max) {
		return redisTemplate.opsForZSet().count(key, min, max);
	}

	@Override
	public <T>void zincrby(String key, T member, double increment) {
		redisTemplate.opsForZSet().incrementScore(key, serializier.serialize(member), increment);
	}

	@Override
	public void zinterStore(String key, Collection<String> otherKeys, String destKey) {
		redisTemplate.opsForZSet().intersectAndStore(key, otherKeys, destKey);
	}

	@Override
	public void zunionStore(String key, Collection<String> otherKeys, String destKey) {
		redisTemplate.opsForZSet().unionAndStore(key, otherKeys, destKey);
	}

	@Override
	public <T>boolean zremove(String key, Collection<T> members) {
		if (members == null || members.size() <= 0) {
			return false;
		}
		List<String> strList = new ArrayList<String>();
		for (T t: members) {
			strList.add(serializier.serialize(t));
		}
		return redisTemplate.opsForZSet().remove(key, strList.toArray(new String[0])) > 0;
	}

	@Override
	public <T>Double zscore(String key, T member) {
		return redisTemplate.opsForZSet().score(key, serializier.serialize(member));
	}

	private boolean setNx(String key, String owner, long timeout) {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/getLockNx.lua")));
		String result = redisTemplate.execute(redisScript, Collections.singletonList(key), serializier.serialize(owner), "" + timeout);
		return result != null && "OK".equalsIgnoreCase(result);
	}
	@SuppressWarnings("unused")
	private boolean setXx(String key, String owner, long timeout) {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/getLockXx.lua")));
		String result = redisTemplate.execute(redisScript, Collections.singletonList(key), serializier.serialize(owner), "" + timeout);
		return result != null && "OK".equalsIgnoreCase(result);
	}
	private boolean setXxEquals(String key, String owner, long timeout) {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/getLockXxEquals.lua")));
		String result = redisTemplate.execute(redisScript, Collections.singletonList(key), serializier.serialize(owner), "" + timeout);
		return result != null && "OK".equalsIgnoreCase(result);
	}

	@Override
	public boolean lock(String key, String owner, long timeout) {
		return lock(key, owner, timeout, false);
	}

	@Override
	public boolean lock(String key, String owner, long timeout, boolean reentrant) {
		return reentrant && setXxEquals(key, owner, timeout) || setNx(key, owner, timeout);
	}

	@Override
	public boolean unlock(String key, String owner) {
		if (!contains(key)) {
			return true;
		}
		DefaultRedisScript<Long> redisScript = new DefaultRedisScript<Long>();
		redisScript.setResultType(Long.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/releaseLock.lua")));
		Long result = redisTemplate.execute(redisScript, Collections.singletonList(key), serializier.serialize(owner));
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
