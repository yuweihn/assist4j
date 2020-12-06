package com.yuweix.assist4j.dao.mybatis.where;




/**
 * 连接符
 * @author yuwei
 */
public enum Connector {
	and("and"),
	or("or");

	private String code;

	Connector(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
