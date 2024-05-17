package com.api.app.email.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.app.email.entities.CustomResponse;
import com.api.app.email.entities.EmailRequest;
import com.api.app.email.entities.EmailRequestMultiple;
import com.api.app.email.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin
@RestController
@RequestMapping("/email")
public class EmailController {

	
	@Autowired
	private EmailService emailService; 
	
	@PostMapping("/send")
	public ResponseEntity<?> sendEmail(@RequestBody EmailRequest emailRequest){
		emailService.sendEmail(emailRequest);
		return ResponseEntity.ok(CustomResponse.builder().httpStatus(HttpStatus.OK).success(true).build());
	}
	
	@PostMapping("/sendMultiple")
	public ResponseEntity<?> sendEmail(@RequestBody EmailRequestMultiple emailRequest){
		emailService.sendEmail(emailRequest);
		return ResponseEntity.ok(CustomResponse.builder().httpStatus(HttpStatus.OK).success(true).build());
	}
	
	@PostMapping("/sendWithFile")
	public ResponseEntity<CustomResponse> sendWithFile(@RequestPart String emailRequest,@RequestPart MultipartFile file) throws IOException{
		ObjectMapper mapper = new ObjectMapper();
		EmailRequestMultiple readValue = mapper.readValue(emailRequest, EmailRequestMultiple.class);
		emailService.sendEmailWithFile(readValue, file);
		return ResponseEntity.ok(CustomResponse.builder().httpStatus(HttpStatus.OK).success(true).build());
	}
	
	@PostMapping("/sendWithHtmlBasic")
	public ResponseEntity<CustomResponse> sendWithHtml(@RequestBody EmailRequestMultiple emailRequest) throws IOException{
	
		emailService.sendEmailWithHtml(emailRequest);
		return ResponseEntity.ok(CustomResponse.builder().httpStatus(HttpStatus.OK).success(true).build());
	}
	
	
	@PostMapping("/sendWithHtmlThymeleaf")
	public ResponseEntity<CustomResponse> sendWithHtmlThymeleaf(@RequestBody EmailRequestMultiple emailRequest) throws IOException{
		emailService.sendWithHtmlThymeleaf(emailRequest);
		return ResponseEntity.ok(CustomResponse.builder().httpStatus(HttpStatus.OK).success(true).build());
	}
}
