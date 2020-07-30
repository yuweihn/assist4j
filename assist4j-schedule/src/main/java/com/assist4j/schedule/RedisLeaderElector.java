package com.assist4j.schedule;




/**
 * @author yuwei
 */
public class RedisLeaderElector extends AbstractLeaderElector {
	private static final String CACHE_LEADER_KEY_PRE = "cache.schedule.leader.";

	private Redis redis;

	/**
	 * timeout in milliseconds
	 */
	private int timeout;


	public RedisLeaderElector(Redis redis, int timeout) {
		super();
		this.redis = redis;
		this.timeout = timeout;
	}

	@Override
	public String acquire(String lock) {
		String key = CACHE_LEADER_KEY_PRE + lock;
		return redis.lock(lock, getLocalNode(key), timeout / 1000);
	}

	@Override
	public void release(String lock) {
		String key = CACHE_LEADER_KEY_PRE + lock;
		redis.unlock(lock, getLocalNode(key));
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}
}
