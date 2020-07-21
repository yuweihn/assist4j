package com.assist4j.data.springboot.jedis;


import com.assist4j.data.cache.redis.jedis.JedisPoolConnFactory;
import com.assist4j.data.cache.redis.jedis.JedisCache;
import com.assist4j.data.serializer.DefaultSerializer;
import com.assist4j.data.serializer.Serializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;


/**
 * 单实例redis
 * @author yuwei
 */
public class JedisConf {

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

	@Bean(name = "jedisConnectionFactory")
	public JedisPoolConnFactory jedisPoolConnFactory(@Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig
			, @Value("${redis.host:}") String host
			, @Value("${redis.port:0}") int port
			, @Value("${redis.dbIndex:0}") int dbIndex
			, @Value("${redis.needPassword:false}") boolean needPassword
			, @Value("${redis.password:}") String password) {
		JedisPoolConnFactory factory = new JedisPoolConnFactory();
		factory.setPoolConfig(jedisPoolConfig);
		if (host != null || !"".equals(host)) {
			factory.setHostName(host);
		}
		if (port > 0) {
			factory.setPort(port);
		}
		if (dbIndex > 0) {
			factory.setDatabase(dbIndex);
		}
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
		template.setEnableDefaultSerializer(true);
//		template.setEnableTransactionSupport(true);
		return template;
	}

	@ConditionalOnMissingBean(Serializer.class)
	@Bean(name = "cacheSerializer")
	public Serializer cacheSerializer() {
		return new DefaultSerializer();
	}

	@ConditionalOnMissingBean(name = "redisCache")
	@Bean(name = "redisCache")
	public JedisCache redisCache(@Qualifier("redisTemplate") RedisTemplate<String, Object> template
			, @Qualifier("cacheSerializer") Serializer serializer) {
		JedisCache cache = new JedisCache();
		cache.setRedisTemplate(template);
		cache.setSerializer(serializer);
		return cache;
	}
}
