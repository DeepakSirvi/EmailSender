package com.api.app.email.entities;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomResponse {

	private String message="Send Successfully";
	
	private HttpStatus httpStatus=HttpStatus.OK;
	
	private boolean success = false;
	
}
