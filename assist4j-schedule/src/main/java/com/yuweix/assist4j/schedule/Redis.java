package com.yuweix.assist4j.schedule;




/**
 * @author yuwei
 */
public interface Redis {
	/**
	 * @param timeout 单位：秒。
	 * 返回锁的持有者。
	 */
	String lock(String key, String owner, long timeout);
	boolean unlock(String key, String owner);
}
