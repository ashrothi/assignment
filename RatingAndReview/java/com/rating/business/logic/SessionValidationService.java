package com.rating.business.logic;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SessionValidationService {

	Boolean validateAuthToken(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException;

	String[] decode(String encoded);

	byte[] restResponseBytes(String error_code, String message) throws IOException;

	

}
