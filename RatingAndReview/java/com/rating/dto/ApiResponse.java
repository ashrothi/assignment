package com.rating.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

	private Integer code;

	private String message;

	private Integer total;

	private Integer noOfResults;

	private Object data;

	private String token;

	private Integer httpStatus;
	
	private Map<String, Object> customMessages;

	public ApiResponse() {
	}
	
	public ApiResponse(String message) {
		this.message = message;
	}
	
	public ApiResponse(Integer httpStatus) {
		this.httpStatus = httpStatus;
	}
	public ApiResponse(Integer httpStatus,String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
	public ApiResponse(Integer httpStatus,Object data) {
		this.httpStatus = httpStatus;
		this.data = data;
	}
	
	public ApiResponse(Integer httpStatus,Object data, Integer total, Integer noOfResults) {
		this.httpStatus = httpStatus;
		this.data = data;
		this.total = total;
		this.noOfResults = noOfResults;
	}
	
	public ApiResponse(Integer httpStatus,Object data, Integer total, Integer noOfResults,Map<String, Object> customMessages) {
		this.httpStatus = httpStatus;
		this.data = data;
		this.total = total;
		this.noOfResults = noOfResults;
		this.customMessages = customMessages;
	}
	
	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getNoOfResults() {
		return noOfResults;
	}

	public void setNoOfResults(Integer noOfResults) {
		this.noOfResults = noOfResults;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Integer getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(Integer httpStatus) {
		this.httpStatus = httpStatus;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, Object> getCustomMessages() {
		return customMessages;
	}

	public void setCustomMessages(Map<String, Object> customMessages) {
		this.customMessages = customMessages;
	}
}
