package com.yuweix.assist4j.data.ds;


import javax.sql.DataSource;


public class KvPair {
	private String key;
	private DataSource value;

	public KvPair(String key, DataSource value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}
	public DataSource getValue() {
		return value;
	}
}
