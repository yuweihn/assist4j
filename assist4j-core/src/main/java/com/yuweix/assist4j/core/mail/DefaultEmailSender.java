package com.yuweix.assist4j.core.mail;


import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yuweix.assist4j.core.Constant;


/**
 * 默认的邮件发送器
 * @author yuwei
 */
public class DefaultEmailSender implements EmailSender<Mail> {
	private static final Logger log = LoggerFactory.getLogger(DefaultEmailSender.class);


	public String server;
	public int sslPort;
	public boolean auth;
	public String user;
	public String password;


	@Override
	public boolean send(Mail mail) {
		Properties props = System.getProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.put("mail.smtp.socketFactory.port", String.valueOf(sslPort));
		props.put("mail.smtp.host", server);
		props.put("mail.smtp.port", String.valueOf(sslPort));
		props.put("mail.smtp.auth", Boolean.toString(auth));
		props.put("mail.smtp.starttls.enable", "true");
		Session session = auth ? Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		}) : Session.getInstance(props);
		session.setDebug(true);

		try {
			MimeMessage msg = new MimeMessage(session);
			InternetAddress address = new InternetAddress();
			address.setPersonal(mail.getSenderName(), Constant.ENCODING_UTF_8);
			address.setAddress(user);
			msg.setFrom(address);
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail.getReceiverEmail(), false));
			msg.setSubject(mail.getTitle(), Constant.ENCODING_UTF_8);
			msg.setSentDate(new Date());
			
			Multipart mp = new MimeMultipart();
			MimeBodyPart mainPart = new MimeBodyPart();
			
			mainPart.setContent(mail.getBody(), "text/html;charset=" + Constant.ENCODING_UTF_8);
			mainPart.setHeader("Content-Transfer-Encoding", "base64");
			mainPart.setHeader("TextSender", mail.getSenderEmail());
			mp.addBodyPart(mainPart);
			
			if (mail.getAttachments() != null) {
				for (DataSource ds : mail.getAttachments()) {
					MimeBodyPart attachment = new MimeBodyPart();
					attachment.setDataHandler(new DataHandler(ds));
					attachment.setFileName(ds.getName());
					attachment.setHeader("Content-Transfer-Encoding", "base64");
					mp.addBodyPart(attachment);
				}
			}

			msg.setContent(mp);
			Transport.send(msg);
			return true;
		} catch (Exception e) {
			log.error("[Error=={}, Receiver=={}]", e.getMessage(), mail.getReceiverEmail());
			throw new RuntimeException(e);
		}
	}



	////////////////////////////////////////////////////////////////////////////////////////
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getSslPort() {
		return sslPort;
	}

	public void setSslPort(int sslPort) {
		this.sslPort = sslPort;
	}

	public boolean getAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
