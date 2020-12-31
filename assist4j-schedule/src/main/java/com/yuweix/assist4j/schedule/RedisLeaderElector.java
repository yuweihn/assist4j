package com.yuweix.assist4j.schedule;




/**
 * @author yuwei
 */
public class RedisLeaderElector extends AbstractLeaderElector {
	private static final String CACHE_LEADER_KEY_PRE = "cache%s.schedule.leader.";

	private Redis redis;

	/**
	 * timeout in milliseconds
	 */
	private int timeout;

	private String appName;


	public RedisLeaderElector(Redis redis, int timeout) {
		this(redis, timeout, null);
	}
	public RedisLeaderElector(Redis redis, int timeout, String appName) {
		super();
		this.redis = redis;
		this.timeout = timeout;
		this.appName = appName;
	}

	@Override
	public String acquire(String lock) {
		String key = String.format(CACHE_LEADER_KEY_PRE
				, this.appName == null || "".equals(this.appName.trim()) ? "" : "." + this.appName.trim()) + lock;
		return redis.lock(key, getLocalNode(), timeout / 1000);
	}

	@Override
	public void release(String lock) {
		String key = String.format(CACHE_LEADER_KEY_PRE
				, this.appName == null || "".equals(this.appName.trim()) ? "" : "." + this.appName.trim()) + lock;
		redis.unlock(key, getLocalNode());
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}
}
