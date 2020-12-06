package com.yuweix.assist4j.http.request;



/**
 * @author yuwei
 */
public class FormField {
	private String key;
	private String value;


	public FormField() {
		
	}

	public FormField(String key, Object value) {
		this.key = key;
		this.value = value.toString();
	}


	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
