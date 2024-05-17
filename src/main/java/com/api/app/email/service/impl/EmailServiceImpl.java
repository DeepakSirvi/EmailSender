package com.api.app.email.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.api.app.email.entities.EmailRequest;
import com.api.app.email.entities.EmailRequestMultiple;
import com.api.app.email.service.EmailService;

import jakarta.mail.Address;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private TemplateEngine templateEngine;
	
	@Value("${spring.mail.username}")
	private String sender; 
	
	@Value("${spring.mail.password}")
	private String password;

	private Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

	@Override
	public void sendEmail(EmailRequest emailRequest) {
		System.err.println("emailservice");
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setTo(emailRequest.getTo());
		simpleMailMessage.setSubject(emailRequest.getSubject());
		simpleMailMessage.setText(emailRequest.getOtp());
		simpleMailMessage.setFrom(sender);
		this.mailSender.send(simpleMailMessage);
		logger.info("email has been sent.. " + emailRequest.getTo() );
	}

	@Override
	public void sendEmail(EmailRequestMultiple emailRequest) {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setTo(emailRequest.getTo());
		simpleMailMessage.setSubject(emailRequest.getSubject());
		simpleMailMessage.setText(emailRequest.getOtp());
		simpleMailMessage.setFrom(sender);
		this.mailSender.send(simpleMailMessage);
		logger.info("email has been sent..to multiple user");

	}
	
	@Override
	public void sendEmailWithFile(EmailRequestMultiple emailRequest, MultipartFile is) {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setTo(emailRequest.getTo());
			helper.setSubject(emailRequest.getSubject());
			helper.setText(emailRequest.getOtp());
			helper.setFrom(sender);
			File file = new File(is.getOriginalFilename());
			Files.copy(is.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			helper.addAttachment(is.getOriginalFilename(), file);
			mailSender.send(mimeMessage);
			logger.info("email has been sent..with file");

		} catch (MessagingException | IOException e) {

			throw new RuntimeException(e);
		}
	}

	@Override
	public void sendEmailWithHtml(EmailRequestMultiple emailRequest) {
		MimeMessage simpleMailMessage = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(simpleMailMessage, true, "UTF-8");
			helper.setTo(emailRequest.getTo());
			helper.setSubject(emailRequest.getSubject());
			helper.setText(emailRequest.getOtp());
			helper.setFrom(sender);
			helper.setText("<h2>hello</h2>", true);
			this.mailSender.send(simpleMailMessage);
			logger.info("email has been sent.. with html");
		} catch (MessagingException e) {

			throw new RuntimeException(e);
		}
		logger.info("email has been sent.. with html" );

	}

	@Override
	public void sendWithHtmlThymeleaf(EmailRequestMultiple emailRequest) {

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587"); // Recommended port
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(sender, password); // Update your actual																			// password here.
			}
		});
		
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(sender));
			Address[] address = new Address[emailRequest.getTo().length];
			int i=0;
			for(String email:emailRequest.getTo()) {
			    address[i++] = new InternetAddress(email);
			}
			
			message.addRecipients(Message.RecipientType.TO,address);
			message.setSubject(emailRequest.getSubject()); // Updated subject
			MimeMultipart multipart = new MimeMultipart("related");
			Context context = new Context();
			context.setVariable("message", emailRequest.getOtp());
			String processedString = templateEngine.process("verification", context);			
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(processedString, "text/html");
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);
			Transport.send(message);
			logger.info("email has been sent..with thyme leaf");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
		
	public void sendEmailWithHtml(String to, String subject) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587"); // Recommended port

		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(sender, password); // Update your actual
																								// password here.
			}
		});
		MimeMessage message = new MimeMessage(session);

		try {
			message.setFrom(new InternetAddress(sender));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject("Email with Embedded Images"); // Updated subject

			MimeMultipart multipart = new MimeMultipart("related");

			// Define the HTML content of the email with placeholders for the embedded
			// images.
			String htmlText = getHtmlTemplate("123456"); // Update this method to handle multiple images

			// Set the HTML content to the MimeBodyPart.
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(htmlText, "text/html");
			// Add the email content part to the multipart.
			multipart.addBodyPart(messageBodyPart);

			// Load and add each image as an embedded image in the email.
//		        String[] imagePaths = {"C:\\Users\\dell\\Downloads\\AIG-Email-Templates\\assets\\svg\\logo.png", "C:\\Users\\dell\\Downloads\\AIG-Email-Templates\\assets\\svg\\verify.png", "C:\\Users\\dell\\Downloads\\AIG-Email-Templates\\assets\\svg\\mac.png","C:\\Users\\dell\\Downloads\\AIG-Email-Templates\\assets\\svg\\playstore.png"}; // Update with actual image paths
//		        int i=0;
//		        for (String imagePath : imagePaths) {
//		            MimeBodyPart imagePart = new MimeBodyPart();
//		            DataSource fds = new FileDataSource(imagePath);
//		            imagePart.setDataHandler(new DataHandler(fds));
//		            // Set the Content-ID header for the image; it matches the "cid" used in the HTML content.
//		            imagePart.setHeader("Content-ID", "<image"+ (++i)+">");
//		            // Add the image part to the multipart.
//		            multipart.addBodyPart(imagePart);
//		        }

			// Combine both the email content and images as the content of the MimeMessage.
			message.setContent(multipart);

			// Send the email with the embedded images.
			Transport.send(message);
			System.out.println("Email Sent");
		} catch (MessagingException e) {
			e.printStackTrace();
		}

//	     MimeMessage simpleMailMessage	= mailSender.createMimeMessage();
//			
//	     try {
//			MimeMessageHelper helper = new MimeMessageHelper(simpleMailMessage,true,"UTF-8");
//			helper.setTo(to);
//			helper.setSubject(subject);
//			helper.setFrom("mayank2k21@gmail.com");
//			helper.setText(getHtmlTemplate("123456"),true);
//			this.mailSender.send(simpleMailMessage);
//			logger.info("email has been sent..");
//		
//	     } catch (MessagingException e) {
//			
//			throw new RuntimeException(e);
//		}
//	     
//	        logger.info("email has been sent..");

	}

	public static String getHtmlTemplate(String message) {
		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<!DOCTYPE html>\n").append("<html lang=\"en\">\n").append("<head>\n")
				.append("<meta charset=\"UTF-8\">\n")
				.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
				.append("<title>AIG Login Verification</title>\n").append("<style>")
				.append("body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f5f5f5; }\n")
				.append(".container { display: flex; flex-direction: column; justify-content: center; align-items: center; padding: 20px; }\n")
				.append(".section { width: 100%; max-width: 600px; margin: auto; }\n")
				.append(".logo { text-align: center; margin-bottom: 20px; }\n")
				.append(".logo img { max-width: 100%; height: auto; }\n")
				.append(".title { font-size: 26px; font-weight: 600; color: #000; margin-top: 30px; text-align: center; }\n")
				.append(".verify_img { max-width: 20%; margin: 20px auto; display: block; }\n")
				.append(".login_otp { border: 2px dashed #1d3577; border-radius: 50px; padding: 6px 0px; text-align: center; font-size: 18px; font-weight: 600; width: 70%; max-width: 300px; margin: 0 auto 24px; color: #1d3577; }\n")
				.append(".subtitle { color: #7A7A7A; font-size: 14px; font-weight: 400; text-align: center; margin-bottom: 30px; }\n")
				.append(".email_btn { color: #FFF; font-size: 14px; font-weight: 500; padding: 8px 36px; border-radius: 36px; background-color: #2B417D; border: none; display: block; margin: 0 auto 30px; text-align: center; }\n")
				.append(".app_details { background-color: #EDEFF5; padding: 40px; border-radius: 8px; margin-top: 40px; }\n")
				.append(".social_media_text { font-size: 20px; font-weight: 700; color: #000; text-align: center; margin-bottom: 24px; }\n")
				.append(".social_media_btn { display: flex; align-items: center; cursor: pointer; padding: 16px 32px; border-radius: 50px; background-color: #000; border: none; color: #fff; margin-bottom: 12px; }\n")
				.append(".social_media_btn h2 { font-size: 12px; color: #7A7A7A; font-weight: 600; margin-bottom: 0px; }\n")
				.append(".social_media_btn h3 { font-size: 18px; color: #fff; margin-bottom: 0; font-weight: 600; }\n")
				.append(".social_media_btn_group {text-align: center; display:flex;align-items: center;cursor: pointer;margin-bottom: 12px;justify-content: space-between;}\n")
				.append(".social_media_btn_group button{ margin-right: 10px;}")
				.append(".social_media_btn_group button:last-child{margin-right: 0;}").append("</style>")
				.append("</head>\n").append("<body>\n").append("<div class=\"container\">\n")
				.append("<div class=\"section\">\n").append("<div class=\"email_templates\">\n")
				.append("<div class=\"email_content\">\n").append("<div class=\"logo\">\n")
				.append("<img src=\"https://res.cloudinary.com/dhgy9as8a/image/upload/v1715927544/logo_qfhd5s.png\">\n")
				.append("</div>\n").append("<h1 class=\"title\">Login Verification</h1>\n")
				.append("<img src=\"https://res.cloudinary.com/dhgy9as8a/image/upload/v1715927545/verify_ohtmnf.png\" class=\"verify_img\">\n")
				.append("<div class=\"login_otp\">" + message + "</div>\n")
				.append("<p class=\"subtitle\">Lorem ipsum dolor sit amet, consectetur adipisicing elit. Delectus, omnis. Blanditiis possimus tempora provident vel, laboriosam nostrum minima alias hic reprehenderit.</p>\n")
				.append("<button class=\"email_btn\">Activate Your Account</button>\n").append("</div>\n")
				.append("<div class=\"app_details\">\n")
				.append("<h2 class=\"social_media_text\">Download the app now!</h2>\n")
				.append("<p class=\"subtitle\">Lorem ipsum dolor sit amet, consectetur adipisicing elit. Delectus, omnis. Blanditiis possimus tempora provident vel, laboriosam nostrum minima alias hic reprehenderit.</p>\n")
				.append("<div class=\"d-flex flex-column\">\n").append("<div class=\"social_media_btn_group\">\n")
				.append("<button class=\"social_media_btn\">\n")
				.append("<img src=\"https://res.cloudinary.com/dhgy9as8a/image/upload/v1715927545/playstore_zaoqnd.png\" height=\"24px\" width=\"24px\">\n")
				.append("<div>\n").append("<h2>GET IT ON</h2>\n").append("<h3>Google Play</h3>\n").append("</div>\n")
				.append("</button>\n").append("<button class=\"social_media_btn\">\n")
				.append("<img src=\"https://res.cloudinary.com/dhgy9as8a/image/upload/v1715927545/mac_pgfnhv.png\" height=\"24px\" width=\"24px\">\n")
				.append("<div>\n").append("<h2>Download on the</h2>\n").append("<h3>App Store</h3>\n")
				.append("</div>\n").append("</button>\n").append("</div>\n").append("</div>\n").append("</div>\n")
				.append("</div>\n").append("</div>\n").append("</div>\n").append("</body>\n").append("</html>");

		return htmlBuilder.toString();
	}

	

}
