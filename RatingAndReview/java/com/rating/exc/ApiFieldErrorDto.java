package com.rating.exc;

import java.util.List;

public class ApiFieldErrorDto extends ApiErrorDto {

	private List<FieldErrorDto> errorFields;

	public ApiFieldErrorDto(Integer errorCode, String message, List<FieldErrorDto> fieldErrors) {
		super(errorCode, message);
		this.errorFields = fieldErrors;
	}
	
	public List<FieldErrorDto> getErrorFields() {
		return errorFields;
	}

	public void setErrorFields(List<FieldErrorDto> errorFields) {
		this.errorFields = errorFields;
	}
	
}