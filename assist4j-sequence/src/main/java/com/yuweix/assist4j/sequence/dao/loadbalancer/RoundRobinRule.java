package com.yuweix.assist4j.sequence.dao.loadbalancer;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.util.Assert;


/**
 * @author yuwei
 */
public class RoundRobinRule implements IRule {
	private int segmentCount;
	private Map<String, AtomicInteger> curSegment = new ConcurrentHashMap<String, AtomicInteger>();
	
	
	public RoundRobinRule() {
		this(1);
	}
	public RoundRobinRule(int segmentCount) {
		setSegmentCount(segmentCount);
	}
	
	
	@Override
	public int chooseSegment(String seqName) {
		Assert.isTrue(segmentCount > 0, "Field segmentCount cannot be less than 1, segmentCount = " + segmentCount);

		AtomicInteger seg = curSegment.get(seqName);
		if (seg == null) {
			seg = new AtomicInteger(0);
			curSegment.put(seqName, seg);
		}

		/**
		 * 查询下一个segment值，并保证范围在[0 ~ segmentCount-1]之间
		 */
		while (true) {
			int curVal = seg.getAndIncrement();
			if (curVal >= 0 && curVal <= segmentCount - 1) {
				return curVal;
			}
			seg.set(0);
		}
	}
	
	@Override
	public void setSegmentCount(int segmentCount) {
		Assert.isTrue(segmentCount > 0, "Parameter segmentCount cannot be less than 1, segmentCount = " + segmentCount);
		this.segmentCount = segmentCount;
	}
}
