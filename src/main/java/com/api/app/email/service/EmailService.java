package com.api.app.email.service;

import org.springframework.web.multipart.MultipartFile;

import com.api.app.email.entities.EmailRequest;
import com.api.app.email.entities.EmailRequestMultiple;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;

public interface EmailService {

	// send email to single persion

	void sendEmail(EmailRequest emailRequest);

	// send email to multiple person
	void sendEmail(EmailRequestMultiple emailRequest);

	// send email with file
	void sendEmailWithFile(EmailRequestMultiple emailRequest, MultipartFile file);

   //send email with html
	void sendEmailWithHtml(EmailRequestMultiple emailRequest);

	void sendEmailWithHtml(String to, String subject);

	void sendWithHtmlThymeleaf(EmailRequestMultiple emailRequest);
}
