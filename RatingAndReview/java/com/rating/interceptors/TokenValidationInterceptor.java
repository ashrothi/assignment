package com.rating.interceptors;

import java.io.IOException;
import java.util.Arrays;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rating.business.logic.TokenValidationService;

/**
 * @author 
 *         Ankita Shrothi TokenValidationInterceptor is a interceptor that
 *         validates authentication token each time whenever a request occurred
 *         to access our secure resource it contains methods that executes
 *         before fullfil the request and after the request end
 */
public class TokenValidationInterceptor extends HandlerInterceptorAdapter {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final Set<String> excludeUrl = new HashSet<String>(
			Arrays.asList("/gcapi/auth", "/gcapi/forgotPassword", "/gcapi/generatePassword", "/gcapi/v1/token",
					"/gcapi/device/switchProfile", "/gcapi/v1/device/switchProfile"));

	@Autowired
	private TokenValidationService tokenValidationService;

	@Resource
	private Environment environment;
	
	

	private static final String REQUEST_ORIGIN_URL = "request.origin.url";
	private static Set<String> origins = null;
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

	/**
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#
	 *      preHandle(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object) Firstly
	 *      External API come in preHandle Process for Authorization
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		request = new RequestWrapper(request);
		System.out.println(request.getRequestURL());
		// handles option request, send true for fake authorization, comment for actual
		// authorization, false for restrict
		if (request.getMethod().equals("OPTIONS")) {
			return true;
		}

		if (StringUtils.isNotBlank(environment.getRequiredProperty(REQUEST_ORIGIN_URL))) {

			// response.setHeader("Access-Control-Allow-Origin", "*");
			String origin = request.getHeader("origin");
			//logger.info("origin in Token Validator: {}", origin);
			if (origin == null) {
				response.setHeader("Access-Control-Allow-Origin", "*");
//			} else if (origins.contains(origin)) {
			} else if (matchOrigin(origin)) {
				response.setHeader("Access-Control-Allow-Origin", origin);
				// return true; // Proceed
			} else {
				logger.warn("Attempted access from non-allowed origin: {}", origin);
				// Include an origin to provide a clear browser error
				response.setHeader("Access-Control-Allow-Origin", origins.iterator().next());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.setStatus(HttpServletResponse.SC_OK);
				
				return false; // No need to find handler
			}

		} else {
			response.setHeader("Access-Control-Allow-Origin", "*");
		}

		// response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers",
				"Content-Type, Access-Control-Allow-Headers, Authorization, TimeZone, X-Requested-With");
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		// endPointUrl variable store the External API URL
		String endPointUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		logger.debug("External APIs: {}", endPointUrl);
		/*
		 * check that API isExcluded or not if endPointUrl variable contain Excluded API
		 * 
		 * @return true
		 */

		if (isExcluded(endPointUrl)) {
			
		
		String apiMapping=	"";
		request.setAttribute("apiId","1");
		request.setAttribute("apiName", "allow");
		logger.info("excluded API Mapping :- "+apiMapping.toString());
		
			return true;
		}
//		List<String> userEndPointUrlList = new ArrayList<>();
		// stored the token in authTokrn Variable
		String authToken = request.getHeader("Authorization");

		if ((authToken == null || authToken.isEmpty()) && (request.getHeader("x-jwt-assertion") == null)) {
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			
//			response.setStatus(configuration.getHttpStatusCode());
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			
			return false;
		}
		return tokenValidationService.validateAuthToken(authToken, endPointUrl, request, response, handler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#
	 * postHandle(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.lang.Object,
	 * org.springframework.web.servlet.ModelAndView)
	 */
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#
	 * afterCompletion(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.lang.Object,
	 * java.lang.Exception)
	 */
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception exceptionIfAny) throws Exception {
	}

	/**
	 * if given url is found in the request then it will all to access the resources
	 * without authentication
	 * 
	 * @param endPointUrl
	 *            - end point url
	 * @return - return true if url found in array else it will return false
	 */
	private boolean isExcluded(String endPointUrl) {
		if (StringUtils.isBlank(endPointUrl))
			return false;
//		return excludeUrl.stream().filter(excluded -> excluded.startsWith(endPointUrl)).findAny().isPresent();
		return excludeUrl.stream().anyMatch(endPointUrl::startsWith);

//		for (String url : excludeUrl) {
//			if (endPointUrl != null && endPointUrl.startsWith(url)) {
//				return true;
//			}
//		}
//		return false;
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

		Map<String, Object> response = new HashMap<String, Object>();
		response.put("errorCode", "500");
		response.put("errorMessage","Inernal server Error");
		response.put("status", "failure");
		String serialized = new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(response);
		return serialized.getBytes();
	}

	private String[] decode(final String encoded) {
		try {
			final byte[] decodedBytes = Base64.decodeBase64(encoded.getBytes());
			final String pair = new String(decodedBytes);
			final String[] userDetails = pair.split(":", 2);
			return userDetails;
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
	
	// private String encode(String userName, String password) {
	// try {
	// final String pair = userName + ":" + password;
	// final byte[] encodedBytes = Base64.encodeBase64(pair.getBytes());
	// return new String(encodedBytes);
	// } catch (Exception e) {
	//
	// }
	// return null;
	// }

	// public static void main(String[] args) {
	// final String pair = "gtadmin:gtadmin";
	// final byte[] encodedBytes = Base64.encodeBase64(pair.getBytes());
	// System.out.println(new String(encodedBytes));
	// TokenValidationInterceptor interceptor = new TokenValidationInterceptor();
	// interceptor.decode(new String(encodedBytes));
	// }

}
