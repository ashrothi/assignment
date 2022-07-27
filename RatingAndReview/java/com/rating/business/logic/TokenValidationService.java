package com.rating.business.logic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface TokenValidationService {

	boolean validateAuthToken(String authToken, String endPointUrl, HttpServletRequest request, HttpServletResponse response, Object handler)
			throws IOException;

}
