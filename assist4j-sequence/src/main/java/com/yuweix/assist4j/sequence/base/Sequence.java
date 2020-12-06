package com.yuweix.assist4j.sequence.base;


/**
 * @author yuwei
 */
public interface Sequence {
	void init();
	void destroy();
	
	long nextValue();
}
