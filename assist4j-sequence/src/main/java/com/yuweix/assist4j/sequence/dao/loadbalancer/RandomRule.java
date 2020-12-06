package com.yuweix.assist4j.sequence.dao.loadbalancer;


import java.util.Random;
import org.springframework.util.Assert;


/**
 * @author yuwei
 */
public class RandomRule implements IRule {
	private Random rand = new Random();
	private int segmentCount;
	
	
	public RandomRule() {
		this(1);
	}
	public RandomRule(int segmentCount) {
		setSegmentCount(segmentCount);
	}
	
	
	@Override
	public int chooseSegment(String seqName) {
		Assert.isTrue(segmentCount > 0, "Field segmentCount cannot be less than 1, segmentCount = " + segmentCount);
		return rand.nextInt(segmentCount);
	}
	
	@Override
	public void setSegmentCount(int segmentCount) {
		Assert.isTrue(segmentCount > 0, "Parameter segmentCount cannot be less than 1, segmentCount = " + segmentCount);
		this.segmentCount = segmentCount;
	}
}
