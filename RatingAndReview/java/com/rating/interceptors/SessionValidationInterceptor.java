package com.rating.interceptors;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.MDC;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.util.Base64Utils;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.rating.bo.User;
import com.rating.business.logic.SessionValidationService;
import com.rating.business.logic.UserService;

/**
 * SessionValidationInterceptor mainly used to intercept the session and
 * validate that session is expired or session is active
 */
public class SessionValidationInterceptor extends HandlerInterceptorAdapter {
	private static final Set<String> excludeUrl = new HashSet<String>(
			Arrays.asList("/api/validate/user", "/api/isLoginSuccess", "/api/setNewPasswrd", "/api/setPasswrd/",
					"/api/resetPasswrd/", "/api/forgotPasswrd", "/api/refresh/token", "/tmpFiles/**",
					"/api/setPasswrd/apiUser/", "/api/resetPasswrd/apiUser/", "/api/setNewPasswrd/apiUser",
					"/api/download/file", "/api/logs", "/api/token/verification", "/api/update/action/history/data",
					"/api/esim/response","/api/generate/encrypted/data","/api/generate/decrypted/data",
					"/api/download/sftp/file",
					"/api/goup/async/activation/notification",
					"/api/goup/async/suspend/notification",
					"/api/goup/async/reactivate/notification",
					"/api/goup/async/terminate/notification",
					"/api/goup/async/attachserviceplan/notification",
					"/api/goup/async/attachdeviceplan/notification",
					"/api/goup/async/downloadandactivate/notification",
					"/api/goup/async/enable/notification",
					"/api/goup/async/delete/notification"));
	
	@Autowired
	@Qualifier("authenticationManager")
	protected AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@Autowired
	private Gson gson;

	@Resource
	private Environment environment;

	@Autowired
	private SessionValidationService sessionValidationService;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final String REQUEST_ORIGIN_URL = "request.origin.url";
	private Set<String> origins = null;
	private static boolean ALLOW_ALL_ORIGINS = false;
	
	@PostConstruct
	public void postConstruct() {
		//allow all origins if value=*
		if(environment.getRequiredProperty(REQUEST_ORIGIN_URL).trim().equals("*")) {
			ALLOW_ALL_ORIGINS = true;
			origins = new HashSet<String>();
		}
		origins = new HashSet<String>(
				Arrays.asList(environment.getRequiredProperty(REQUEST_ORIGIN_URL).toString().trim().split(",")));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#
	 * preHandle(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		request = new RequestWrapper(request);
		System.out.println(request.getRequestURL());
		boolean status = (boolean) ((RequestWrapper) request).getValidationStatus(request);
		// if status is false then return from there and give the unauthorized message
		if (status) {
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getOutputStream().write(sessionValidationService.restResponseBytes("401",
					"There is a violation of the security rule element in the api request you accessed, denied access!"));
			return false;
		}

		// response.setHeader("Access-Control-Allow-Origin",
		// request.getHeader("origin"));
		// response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE,
		// OPTIONS");
		// response.setHeader("Access-Control-Allow-Headers",
		// "Content-Type, Access-Control-Allow-Headers, Authorization, TimeZone,
		// X-Requested-With, accountId");
		// response.setHeader("Access-Control-Allow-Credentials", "true");
		// response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		// Origin code commented Because Many ThirdParty used apis.
		String endPointUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

		if (StringUtils.isNotBlank(environment.getRequiredProperty(REQUEST_ORIGIN_URL))) {

			String origin = request.getHeader("origin");
			// logger.info("origin in session Validator: {}", origin);
			if (origin == null) {
				response.setHeader("Access-Control-Allow-Origin", "*");
				// } else if (origins.contains(origin)) {
			} else if (matchOrigin(origin)) {
				response.setHeader("Access-Control-Allow-Origin", origin);
				response.addHeader("Host", origin);
				// return true; // Proceed
			} else if (endPointUrl.startsWith("/api/download/file/**")
					|| endPointUrl.startsWith("/api/download/file")) {
				// Proceed
				response.setHeader("Access-Control-Allow-Origin", "*");
				response.addHeader("Host", "*");
			} else {
				logger.warn("Attempted access from non-allowed origin: {}", origin);

				// Include an origin to provide a clear browser error
				response.setHeader("Access-Control-Allow-Origin", origins.iterator().next());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setStatus(HttpServletResponse.SC_OK);
				

				return false; // No need to find handler
			}
		} else {
			response.setHeader("Access-Control-Allow-Origin", "*");
		}
		// Set For initial State
		// response.setHeader("Access-Control-Allow-Origin",
		// request.getHeader("origin"));

		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
		response.setHeader("Access-Control-Allow-Headers",
				"Content-Type, Access-Control-Allow-Headers, Authorization, TimeZone, Language, X-Requested-With, accountId, UTCminutes");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("X-XSS-Protection", "1; mode=block");
		response.setHeader("Referrer-Policy", "same-origin");
		response.setHeader("Server", "Play");
		// already set in SecurityConfig .frameOptions().deny();
		// response.setHeader("X-Frame-Options", "DENY");
		response.setHeader("X-Download-Options", "noopen");
		response.setHeader("X-Content-Type-Options", "nosniff");
		response.setHeader("Content-Security-Policy", "default-src 'self';img-src *");
		response.setHeader("Allow", "OPTIONS, GET, POST, PUT, DELETE");
		//appsec security headers
		response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
		response.addHeader("Content-Security-Policy", "default-src 'self'");
		
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		// if endPointUrl is excluded then return true
		// if request method is Options then skip validation and return true from here
		if (isExcluded(endPointUrl)) {
			return true;
		}

		// handles option request, send true for fake authorization, comment for actual
		// authorization, false for restrict
		if (request.getMethod().equals("OPTIONS")) {
			return true;
		}

		String authToken = request.getHeader("Authorization");

		// if authToken is empty then return false and send Unauthorized message
		if ((authToken == null || authToken.isEmpty()) && (request.getHeader("x-jwt-assertion") == null)) {
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			
			return false;
		}

		User user = null;
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = headerNames.nextElement();
			request.setAttribute(key, request.getHeader(key));
			// logger.debug("key: {}. Value: {}.", key, request.getHeader(key));
		}

		if (request.getHeader("x-jwt-assertion") != null) {
			String[] splitString = request.getHeader("x-jwt-assertion").split("\\.");
			String base64EncodedBody = splitString[1];

			String decodedBody = new String(Base64Utils.decode(base64EncodedBody.getBytes()));
			Map<String, Object> jsonObject = gson.fromJson(decodedBody, HashMap.class);

			// logger.debug("Decoded Json Object:{}", jsonObject);
			String userVal = jsonObject.get("http://wso2.org/claims/enduser").toString();
			String userName = userVal.substring(0, userVal.indexOf('@'));
			user = userService.getUserByUserName(userName);
			// check user is valid or not, check user by user name
			// if user is not found in the database then stop the
			// the request with unauthorized access
			if (user == null) {
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				
				
				return false;
			}
			logger.debug("Got User profile: {}", userName);

			// add username into MDC of the thread
			MDC.put("UserName", user.getUserName());
		} else if (authToken != null) {
			// validates auth token
			Boolean decision = sessionValidationService.validateAuthToken(request, response, handler);
			// forward the return if any(not null)
			if (decision != null)
				return decision;
		}
		return true;
	}

	/**
	 * if given url is found in the reques then it will all to access the resouces
	 * without authentication
	 * 
	 * @param endPointUrl
	 *            - end point url
	 * @return - return true if url found in array else it will return false
	 */
	private boolean isExcluded(String endPointUrl) {
		if(StringUtils.isBlank(endPointUrl))
			return false;
		return excludeUrl.stream().anyMatch(endPointUrl::startsWith);
	}

	/**
	 * convert the rest response into bytes
	 * 
	 * @param configuration
	 *            - SystemConfiguration object
	 * @return - it returns bytes array
	 * @throws IOException
	 */
	private byte[] restResponseBytes() throws IOException {

		Map<String, Object> response = new HashMap<>();
		response.put("errorCode", "500");
		response.put("errorMessage", "Internal Server error");
		response.put("status", "failure");
		String serialized = new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(response);
		return serialized.getBytes();
	}

	private String[] decode(final String encoded) {
		try {
			final byte[] decodedBytes = Base64.decodeBase64(encoded.getBytes());
			final String pair = new String(decodedBytes);
			return pair.split(":", 2);
		} catch (Exception e) {

		}
		return null;
	}

	private boolean matchOrigin(String origin) {
//		if origins property value=* // allow always
		if(ALLOW_ALL_ORIGINS)
			return true;
		
//		if(StringUtils.isBlank(origin))
//			return false;
		
		//i.startsWith(origin)//when found then return true // here i will be allowed origins in stream
		if (origins.stream().anyMatch(origin::startsWith))
			return true;
		
		return false;
	}
	
}
