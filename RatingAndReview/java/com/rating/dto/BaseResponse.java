package com.rating.dto;

import java.io.Serializable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * base class for all REST api response pojos. used to hold parameters(status
 * code in error case, exception object and more captured things.)
 * 
 * @author Ankita Shrothi
 *
 */
public class BaseResponse extends Object implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private HttpStatus statusCode;

	@JsonIgnore
	private String responeBodyAsString;

	@JsonIgnore
	private HttpHeaders headers;

	@JsonIgnore
	private HttpStatusCodeException httpException;

	public String getResponeBodyAsString() {
		return responeBodyAsString;
	}

	public void setResponeBodyAsString(String responeBodyAsString) {
		this.responeBodyAsString = responeBodyAsString;
	}

	public HttpStatus getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(HttpStatus statusCode) {
		this.statusCode = statusCode;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public void setHeaders(HttpHeaders headers) {
		this.headers = headers;
	}

	public HttpStatusCodeException getHttpException() {
		return httpException;
	}

	public void setHttpException(HttpStatusCodeException httpException) {
		this.httpException = httpException;
	}

}
