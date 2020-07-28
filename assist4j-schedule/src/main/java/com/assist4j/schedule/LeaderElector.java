package com.assist4j.schedule;




/**
 * @author yuwei
 */
public interface LeaderElector {
	boolean acquire();
	void release();
	String getLocalNode();
	String getLeaderNode();
}
