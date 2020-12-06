package com.yuweix.assist4j.core.encrypt;


/**
 * @author yuwei
 */
public enum Algor {
	MD5("MD5"),
	SHA1("SHA-1"),
	DES("DES");

	private String code;
	Algor(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
