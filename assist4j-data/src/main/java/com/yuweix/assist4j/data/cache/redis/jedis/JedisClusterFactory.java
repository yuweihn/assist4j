package com.yuweix.assist4j.data.cache.redis.jedis;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;


/**
 * @author wei
 */
public class JedisClusterFactory implements FactoryBean<JedisCluster> {
	private List<HostAndPort> redisNodeList;
	private JedisCluster jedisCluster;
	private int timeout;
	private int maxRedirections;
	private GenericObjectPoolConfig jedisPoolConfig;




	@Override
	public JedisCluster getObject() throws Exception {
		return jedisCluster;
	}

	@Override
	public Class<? extends JedisCluster> getObjectType() {
		return this.jedisCluster != null ? this.jedisCluster.getClass() : JedisCluster.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void init() {
		Assert.notEmpty(redisNodeList, "[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
		Set<HostAndPort> nodes = new HashSet<HostAndPort>(redisNodeList);
		jedisCluster = new JedisCluster(nodes, timeout, maxRedirections, jedisPoolConfig);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void setRedisNodeList(List<HostAndPort> redisNodeList) {
		this.redisNodeList = redisNodeList;
	}

	public List<HostAndPort> getRedisNodeList() {
		return redisNodeList;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getMaxRedirections() {
		return maxRedirections;
	}

	public void setMaxRedirections(int maxRedirections) {
		this.maxRedirections = maxRedirections;
	}

	public GenericObjectPoolConfig getJedisPoolConfig() {
		return jedisPoolConfig;
	}

	public void setJedisPoolConfig(GenericObjectPoolConfig jedisPoolConfig) {
		this.jedisPoolConfig = jedisPoolConfig;
	}
}
