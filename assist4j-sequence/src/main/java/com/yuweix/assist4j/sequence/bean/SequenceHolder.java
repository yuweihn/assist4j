package com.yuweix.assist4j.sequence.bean;


import java.util.concurrent.atomic.AtomicLong;
import com.yuweix.assist4j.sequence.exception.SequenceException;


/**
 * @author yuwei
 */
public class SequenceHolder {
	private final long ceil;
	private final AtomicLong currentValue;
	
	public SequenceHolder(long min, long max) {
		if (min <= 0) {
			throw new SequenceException("[min] must be larger then 0.");
		}
		if (min >= max) {
			throw new SequenceException("[max] must be larger then [min].");
		}
		
		this.currentValue = new AtomicLong(min);
		this.ceil = max;
	}
	
	/**
	 * 获取一个值，如果超出范围，返回-1。
	 */
	public long getAndIncrement() {
		long val = currentValue.getAndIncrement();
		if (val > ceil) {
			return -1L;
		}
		return val;
	}
	
	public long get() {
		long val = currentValue.get();
		if (val > ceil) {
			return -1L;
		}
		return val;
	}
}
