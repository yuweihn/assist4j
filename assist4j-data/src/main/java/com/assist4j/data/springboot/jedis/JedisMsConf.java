package com.assist4j.data.springboot.jedis;


import com.assist4j.data.cache.redis.jedis.JedisPoolConnFactory;
import com.assist4j.data.cache.redis.jedis.JedisCache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;


/**
 * 一主多从redis
 * @author yuwei
 */
public class JedisMsConf {
	@Bean(name = "jedisPoolConfig")
	public JedisPoolConfig jedisPoolConfig(@Value("${redis.pool.maxTotal:1024}") int maxTotal
			, @Value("${redis.pool.maxIdle:100}") int maxIdle
			, @Value("${redis.pool.minIdle:100}") int minIdle
			, @Value("${redis.pool.maxWaitMillis:10000}") long maxWaitMillis
			, @Value("${redis.pool.testOnBorrow:false}") boolean testOnBorrow) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		config.setMinIdle(minIdle);
		config.setMaxWaitMillis(maxWaitMillis);
		config.setTestOnBorrow(testOnBorrow);
		return config;
	}

	@Bean(name = "redisSentinelConfiguration")
	public RedisSentinelConfiguration redisSentinelConfiguration(@Value("${redis.master.name}") String masterName
			, @Value("${redis.sentinel.ip}") String host
			, @Value("${redis.sentinel.port}") int port) {
		RedisSentinelConfiguration conf = new RedisSentinelConfiguration();
		RedisNode redisNode = new RedisNode.RedisNodeBuilder().withName(masterName).build();
		conf.setMaster(redisNode);

		Set<RedisNode> sentinels = new HashSet<RedisNode>();
		sentinels.add(new RedisNode(host, port));
		conf.setSentinels(sentinels);
		return conf;
	}

	@Bean(name = "jedisConnectionFactory")
	public JedisPoolConnFactory jedisPoolConnFactory(@Qualifier("redisSentinelConfiguration") RedisSentinelConfiguration sentinelConfig
			, @Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig
			, @Value("${redis.dbIndex:0}") int dbIndex
			, @Value("${redis.needPassword:false}") boolean needPassword
			, @Value("${redis.password:}") String password) {
		JedisPoolConnFactory factory = new JedisPoolConnFactory(sentinelConfig);
		factory.setPoolConfig(jedisPoolConfig);
		factory.setDatabase(dbIndex);
		factory.setNeedPassword(needPassword);
		factory.setPassword(password);
		return factory;
	}

	@Bean(name = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate(@Qualifier("jedisConnectionFactory") RedisConnectionFactory connFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(connFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		return template;
	}

	@ConditionalOnMissingBean(name = "redisCache")
	@Bean(name = "redisCache")
	public JedisCache redisCache(@Qualifier("redisTemplate") RedisTemplate<String, Object> template) {
		JedisCache cache = new JedisCache();
		cache.setRedisTemplate(template);
		return cache;
	}
}
