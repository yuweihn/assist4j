package com.yuweix.assist4j.core;


import com.alibaba.fastjson.JSONObject;
import java.io.Serializable;


/**
 * @author yuwei
 */
public class Response<C, D> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private C code;
	private String msg;
	private D data;

	public Response() {

	}
	public Response(C code, String msg) {
		this(code, msg, null);
	}
	public Response(C code, String msg, D data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}


	public C getCode() {
		return code;
	}
	public void setCode(C code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public D getData() {
		return data;
	}
	public void setData(D data) {
		this.data = data;
	}
}
