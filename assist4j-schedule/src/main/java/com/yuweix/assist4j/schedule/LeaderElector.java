package com.yuweix.assist4j.schedule;




/**
 * Leader选择器
 * @author yuwei
 */
public interface LeaderElector {
	String acquire(String lock);
	void release(String lock);
	String getLocalNode();
}
