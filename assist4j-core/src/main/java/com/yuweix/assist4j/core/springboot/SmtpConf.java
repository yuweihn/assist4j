package com.yuweix.assist4j.core.springboot;


import com.yuweix.assist4j.core.mail.DefaultEmailSender;
import com.yuweix.assist4j.core.mail.EmailSender;
import com.yuweix.assist4j.core.mail.Mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;


/**
 * @author yuwei
 */
public class SmtpConf {
	@Bean(name = "defaultEmailSender")
	public EmailSender<Mail> defaultSender(@Value("${assist4j.smtp.server}") String server
			, @Value("${assist4j.smtp.ssl.port}") int sslPort
			, @Value("${assist4j.smtp.auth}") boolean auth
			, @Value("${assist4j.smtp.user}") String user
			, @Value("${assist4j.smtp.password}") String password) {
		DefaultEmailSender defaultSender = new DefaultEmailSender();
		defaultSender.setServer(server);
		defaultSender.setSslPort(sslPort);
		defaultSender.setAuth(auth);
		defaultSender.setUser(user);
		defaultSender.setPassword(password);
		return defaultSender;
	}
}
