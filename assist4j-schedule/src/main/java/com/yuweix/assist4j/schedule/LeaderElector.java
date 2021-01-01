package com.yuweix.assist4j.schedule;




/**
 * Leader选择器
 * @author yuwei
 */
public interface LeaderElector {
	/**
	 * 尝试获取锁，返回锁的持有者。
	 * @param lock
	 * @return
	 */
	String acquire(String lock);
	void release(String lock);
	String getLocalNode();
}
