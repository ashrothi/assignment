package com.rating.react.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import com.rating.dto.ApiResponse;
import com.rating.exc.ApiErrorDto;
import com.rating.exc.ApiException;
import com.rating.exc.ApiFieldErrorDto;
import com.rating.exc.FieldErrorDto;
import com.rating.business.logic.AuthService;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Ankita Shrothi
 * 
 *         This controller use for external apis. Here exists some methods for
 *         actions like GET, POST, PUT, DELETE etc.
 * 
 */
@ApiIgnore
@RestController
@Transactional("RatingAndReviewTransactionManager")
public class ApiController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	
	
	@Autowired
	private AuthService authService;
	
	@Resource
	private Environment env;

	private static final Set<String> excludeUrl = new HashSet<>(Arrays.asList("/gcapi/auth"));
	


	/**
	 * @param requestParams
	 * @param request
	 * @return response
	 * 
	 *         postAction method is used when the Request External API is in the
	 *         form of POST case:POST
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PostMapping(value = "/gcapi/**")
	public ApiResponse postAction(@RequestBody(required = false) Map<String, Object> requestParams,
			HttpServletRequest request) throws Exception {
		String endPointUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		// Get API mapping details using endpoint url.
		
		
		/**
		 * Set required parameters.
		 */
		
		requestParams.put("loggedInUserId", request.getAttribute("loggedInUserId"));
		requestParams.put("loggedInUserName", request.getAttribute("loggedInUserName"));
		requestParams.put("requestId", request.getAttribute("requestId"));
		requestParams.put("apiId", request.getAttribute("apiId"));
		requestParams.put("apiName", request.getAttribute("apiName"));
		requestParams.put("executeApiUrl", endPointUrl);

		/**
		 * If Param values in query string, then set in request param.
		 */
		addQueryStringParameters(request, requestParams);

		
		logger.info("requestParams:{}",requestParams);
		
		

		logger.debug(
				"End Point : " + endPointUrl + "\nRequest Body : " + maskSesitiveInfo(new JSONObject(requestParams)));
		ApiResponse response = authService.getToken(requestParams);
	
		return response;
	}

	
	

	

	/**
	 * Method get param from query string and put in request param map.
	 * 
	 * @param request
	 * @param requestParams
	 */
	private void addQueryStringParameters(HttpServletRequest request, Map<String, Object> requestParams) {

		String queryString = request.getQueryString();
		if (!StringUtils.isEmpty(queryString)) {
			String[] parameters = queryString.split("&");

			for (String parameter : parameters) {
				String[] keyValuePair = parameter.split("=");

				String value = "";
				try {
					value = keyValuePair[1];
				} catch (Exception e) {
					value = "";
				}
				requestParams.put(keyValuePair[0], value);
			}
		}
	}

	/**
	 * Method used for exception handling.
	 * 
	 * When we get Response from internal method and in the response if we get any
	 * exception.
	 * 
	 * Then we used this method to handle exception and send the proper response.
	 * 
	 * @param apiException
	 * @param request
	 * @return
	 */
	@ExceptionHandler(value = ApiException.class)
	protected ResponseEntity<ApiErrorDto> handleApiErrorDto(ApiException apiException, HttpServletRequest request) {
		ResponseStatus responseStatus = AnnotationUtils.findAnnotation(apiException.getClass(), ResponseStatus.class);
		HttpStatus status;
		if (apiException.getHttpStatus() != null) {
			status = HttpStatus.valueOf(apiException.getHttpStatus());
		} else {
			status = responseStatus.value();
		}
		return new ResponseEntity<>(apiException.getApiErrorDto(), status);
	}

	/**
	 * 
	 * Method used to handle exceptions. If we get any request binding exception
	 * Then we use this method to handle exception (Basically this method used to
	 * handle any validation errors).
	 * 
	 * @param bindException
	 * @param request
	 * @return
	 */
	@ExceptionHandler(value = BindException.class)
	protected ResponseEntity<ApiFieldErrorDto> handleBindingException(BindException bindException,
			HttpServletRequest request) {

		List<FieldErrorDto> fieldErrorDtos = new ArrayList<>();

		List<FieldError> fieldErrors = bindException.getBindingResult().getFieldErrors();
		for (FieldError fieldError : fieldErrors) {
			FieldErrorDto fieldErrorDto = new FieldErrorDto();
			fieldErrorDto.setField(fieldError.getField());
			
			fieldErrorDto.setRejectedValue(fieldError.getRejectedValue());
			fieldErrorDtos.add(fieldErrorDto);
		}

		return new ResponseEntity<>(
				new ApiFieldErrorDto(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Validation error", fieldErrorDtos),
				HttpStatus.UNPROCESSABLE_ENTITY);

	}

	/**
	 * Masks sensitive data fields like passwords.
	 * 
	 * @param requestBody
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private JSONObject maskSesitiveInfo(JSONObject requestBody) {
		try {
			requestBody.replace("password", "******");
			requestBody.replace("currentPassword", "******");
			requestBody.replace("newPassword", "******");
		} catch (Exception e) {
			logger.debug(ExceptionUtils.getStackTrace(e));
		}
		return requestBody;
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
		if(org.apache.commons.lang3.StringUtils.isBlank(endPointUrl))
			return false;
		return excludeUrl.stream().anyMatch(endPointUrl::startsWith);
	}
}
