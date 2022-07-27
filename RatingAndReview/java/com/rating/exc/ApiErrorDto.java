package com.rating.exc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiErrorDto {

	private Integer errorCode;
	private String errorMessage;
	
	public ApiErrorDto(Integer errorCode, String message) {
		this.errorCode = errorCode;
		this.errorMessage = message;
		
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}