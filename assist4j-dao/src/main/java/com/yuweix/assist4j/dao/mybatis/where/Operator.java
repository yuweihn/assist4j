package com.yuweix.assist4j.dao.mybatis.where;




/**
 * 操作符
 * @author yuwei
 */
public enum Operator {
	gt(">"),
	gte(">="),
	eq("="),
	lt("<"),
	lte("<="),
	ne("!="),
	like("like"),
	isNull("is null"),
	isNotNull("is not null");

	private String code;

	Operator(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
