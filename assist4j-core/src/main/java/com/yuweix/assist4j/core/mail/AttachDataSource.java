package com.yuweix.assist4j.core.mail;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;


/**
 * 附件
 * @author yuwei
 */
public class AttachDataSource implements DataSource {
	private byte[] attachData;
	/**
	 * 目标对象的类型
	 **/
	private String contentType;
	/**
	 * 目标对象的名称
	 **/
	private String name;
	
	public AttachDataSource(byte[] attachData, String contentType, String name) {
		this.attachData = attachData;
		this.contentType = contentType;
		this.name = name;
	}
	
	
	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(attachData);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return null;
	}
}
