package com.yuweix.assist4j.data.cache.redis.jedis;


import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


/**
 * @author yuwei
 */
public class JedisPoolConnFactory extends JedisConnectionFactory {
	private boolean needPassword;


	public JedisPoolConnFactory() {
		
	}


	public JedisPoolConnFactory(RedisSentinelConfiguration sentinelConfig) {
		super(sentinelConfig);
	}


	@Override
	public void setHostName(String hostName) {
		if (!StringUtils.isEmpty(hostName)) {
			super.setHostName(hostName);
		}
	}

	@Override
	public void setPort(int port) {
		Assert.isTrue(port > 0, "Invalid port.");
		super.setPort(port);
	}

	@Override
	public void setDatabase(int dbIndex) {
		Assert.isTrue(dbIndex >= 0, "Invalid DB index (a positive dbIndex required)");
		super.setDatabase(dbIndex);
	}

	public void setNeedPassword(boolean needPassword) {
		this.needPassword = needPassword;
	}

	@Override
	public void setPassword(String password) {
		if (needPassword) {
			super.setPassword(password);
		}
	}
}
