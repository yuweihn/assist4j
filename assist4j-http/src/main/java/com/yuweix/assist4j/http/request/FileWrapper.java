package com.yuweix.assist4j.http.request;


/**
 * @author yuwei
 */
public class FileWrapper {
	private byte[] content;
	/**
	 * 文件名。
	 * 如Koala.jpg
	 */
	private String fileName;


	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
