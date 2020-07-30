package com.assist4j.schedule;




/**
 * @author yuwei
 */
public interface LeaderElector {
	String acquire(String lock);
	void release(String lock);
	String getLocalNode(String lock);
}
