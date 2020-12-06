package com.yuweix.assist4j.core.mail;




/**
 * 邮件发送接口
 * @author yuwei
 */
public interface EmailSender<T> {
	boolean send(T mail);
}
