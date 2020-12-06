package com.yuweix.assist4j.sequence.base;


import java.util.Map;


/**
 * @author yuwei
 **/
public interface SequenceBeanHolder {
	/**
	 * 返回键值对格式数据，如果“值”为空，就以“键”作为“值”。
	 * 键：beanName
	 * 值：seqName,minValue
	 *
	 * 示例：seqUser: seq_user,100
	 * 表示注册到Spring容器中的Bean实例以seqUser为beanName，同时存入数据库的记录的名字为seq_user，并保证最小值为100
	 */
	Map<String, String> getSequenceMap();
}
