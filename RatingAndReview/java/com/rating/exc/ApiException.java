package com.rating.exc;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ApiException extends Exception {

	private static final long serialVersionUID = 1L;

	private final Integer httpStatus;

	private final ApiErrorDto apiErrorDto;

	public ApiException(Integer httpStatus, ApiErrorDto apiErrorDto) {
		this.httpStatus = httpStatus;
		this.apiErrorDto = apiErrorDto;
	}

	public Integer getHttpStatus() {
		return httpStatus;
	}

	public ApiErrorDto getApiErrorDto() {
		return this.apiErrorDto;
	}

}