package com.example.demo.service;


import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    
	
	@Value("${custom.mail.from}")
	private String fromEmail;
	
	@Value("${custom.mail.google.app.password}")
	private String googleAppPassword;
	
	@Value("${custom.mail.smtp.host}")
	private String smptpHost;
	
	@Value("${custom.mail.smtp.port}")
	private String smtpPort;

	
	/**
     * 通用的郵件發送方法。
     * @param to 收件人郵箱
     * @param subject 郵件主題
     * @param htmlContent 郵件內容 (HTML格式)
     */
	
	public void sendHtmlEmail(String to,String subject,String htmlContent) {
		Properties properties = new Properties();
		properties.put("mail.smtp.auth","true");
		properties.put("mail.smtp.starttls.enable","true");
		properties.put("mail.smtp.host","smtpHost");
		properties.put("mail.smtp.port","smtpPort");
		
		Session session = Session.getInstance(properties,new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail,googleAppPassword);
			}
		});
		
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject,"UTF-8");// 指定編碼
			message.setContent(htmlContent,"text/html;charset=utf-8");// 設置為 HTML 格式並指定編碼
			Transport.send(message);
			System.out.println("郵件已成功發送至: "+to);// 建議使用 Logger
		} catch (MessagingException e) {
			System.err.println("發送郵件失敗到 " + to + ": " + e.getMessage()); // 建議使用 Logger
            // 在實際應用中，你可能想向上拋出一個運行時異常，以便調用者可以處理
            // throw new RuntimeException("郵件發送失敗", e);

		}
	}
	
	
}
