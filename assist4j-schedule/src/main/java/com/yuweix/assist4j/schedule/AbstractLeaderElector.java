package com.yuweix.assist4j.schedule;


import com.yuweix.assist4j.schedule.util.IpUtil;


/**
 * @author yuwei
 */
public abstract class AbstractLeaderElector implements LeaderElector {
	private final String localNode;

	public AbstractLeaderElector() {
		this.localNode = IpUtil.getLocalInnerIP();
	}

	@Override
	public String getLocalNode(String key) {
		return localNode;
	}

	abstract void init();
	abstract void destroy();
}
