package com.uslunchbox.restaurant.utils;

import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

	private static final String SMTP_HOST_NAME = "smtp.gmail.com";
	private static final String SMTP_PORT = "465";
	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

	public void sendSSLMessage(String recipients[], String[] CCs,
			String[] BCCs, String subject, String message, String from)
			throws MessagingException {

		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

		Properties props = new Properties();
		props.put("mail.smtp.user", "WebMaster");
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", SMTP_PORT);
		props.put("mail.smtp.socketFactory.port", SMTP_PORT);
		props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.put("mail.smtp.socketFactory.fallback", "false");

		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(
								"uslunchbox@gmail.com", "3053481218");// modify
					}
				});

		Message msg = new MimeMessage(session);
		InternetAddress addressFrom = null;
		try {
			addressFrom = new InternetAddress(from, "USLUNCHBOX");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		msg.setFrom(addressFrom);
		if (recipients != null) {
			InternetAddress[] addressTo = new InternetAddress[recipients.length];
			for (int i = 0; i < recipients.length; i++) {
				addressTo[i] = new InternetAddress(recipients[i]);
			}
			msg.setRecipients(Message.RecipientType.TO, addressTo);
		}
		if(CCs != null){
			InternetAddress[] addressTo = new InternetAddress[CCs.length];
			for (int i = 0; i < CCs.length; i++) {
				addressTo[i] = new InternetAddress(CCs[i]);
			}
			msg.setRecipients(Message.RecipientType.CC, addressTo);
		}
		if(BCCs != null){
			InternetAddress[] addressTo = new InternetAddress[BCCs.length];
			for (int i = 0; i < BCCs.length; i++) {
				addressTo[i] = new InternetAddress(BCCs[i]);
			}
			msg.setRecipients(Message.RecipientType.BCC, addressTo);
		}

		// Setting the Subject and Content Type
		msg.setSubject(subject);
		msg.setContent(message, "text/html");
		msg.setHeader("Content-type", "text/html");
		Transport.send(msg);
	}

}