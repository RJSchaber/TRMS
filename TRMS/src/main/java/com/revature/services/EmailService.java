package com.revature.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import com.revature.models.Employee;
import com.revature.utils.S3Util;

public class EmailService implements EmailServiceInterface {
	private static Logger log = LogManager.getLogger(EmailService.class);

	   public void SendEmail(String subject, String body) {
	      // Recipient's email ID needs to be mentioned.
	      String to = "richard.schaber@revature.net";

	      final String username = System.getenv("EMAIL_ADDRESS");
	      final String password = System.getenv("EMAIL_PASSWORD");
	      
	      // Sender's email ID needs to be mentioned
	      String from = username;

	      // use gmails smtp
	      String host = "smtp.gmail.com";

	      // Get system properties
	      Properties properties = new Properties();

	      // Setup mail server
	      properties.put("mail.smtp.host", host);
	      properties.put("mail.smtp.port", "587");
	      properties.put("mail.smtp.auth", "true");
	      properties.put("mail.smtp.starttls.enable", "true");
	      properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
	      
	      Session session = Session.getInstance(properties,
	                new javax.mail.Authenticator() {
	                    protected PasswordAuthentication getPasswordAuthentication() {
	                        return new PasswordAuthentication(username, password);
	                    }
	                });
	      try {
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

	         // Set Subject: header field
	         message.setSubject(subject);

	         // Send the actual HTML message, as big as you like
	         message.setContent(body, "text/html");

	         // Send message
	         Transport.send(message);
	         log.trace("Sent message successfully....");
	         
	      } catch (MessagingException mex) {
	         mex.printStackTrace();
	      }
	   }
}
