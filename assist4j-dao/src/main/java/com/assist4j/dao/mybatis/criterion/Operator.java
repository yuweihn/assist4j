package com.assist4j.dao.mybatis.criterion;




/**
 * 操作符
 * @author yuwei
 */
public enum Operator {
	gt(" > "),
	gte(" >= "),
	eq(" = "),
	lt(" < "),
	lte(" <= "),
	ne(" != "),
	like(" like ");

	private String code;

	Operator(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
