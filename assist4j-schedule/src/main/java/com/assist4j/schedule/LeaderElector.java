package com.assist4j.schedule;




/**
 * @author yuwei
 */
public interface LeaderElector {
	boolean acquire(String lock);
	void release(String lock);
	String getLocalNode(String lock);
	String getLeaderNode(String lock);
}
