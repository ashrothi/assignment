package com.rating.react.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rating.utils.PasswordUtil;
import com.rating.bo.User;
import com.rating.business.logic.UserService;

@Controller
@Transactional(transactionManager = "RatingAndReviewTransactionManager")
public class PasswrdController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserService userService;

	

	@Resource
	private Environment env;

	private static final String ERROR_STATUS = "Error";
	private static final String STATUS = "status";
	private static final String MESSAGE = "message";
	private static final String SUCCESS = "Success";

	/**
	 * This API is used for forgot password for React UI integration.
	 * 
	 * @param userName
	 * @param mailId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/api/forgotPasswrd")
	public ResponseEntity<Map<String, String>> forgotPasswrd(String userName, String mailId,
			HttpServletRequest request) {
		Map<String, String> responseMap = new HashMap<>();
		try {
			User userProfile = userService.getUserByUserNameOrEmail(userName);
			if (userProfile == null ) {
				responseMap.put(STATUS, ERROR_STATUS);
				responseMap.put(MESSAGE, "Invalid User Name or Email ID.");
				return new ResponseEntity<>(responseMap, HttpStatus.OK);
			} else {

				if (userProfile.getUserAction() == 2) {
					responseMap.put(STATUS, SUCCESS);
					responseMap.put(MESSAGE,
							"Already Send reset password email on your account. Please Check your email.");
					return new ResponseEntity<>(responseMap, HttpStatus.OK);
				}

				String baseUrl = request.getHeader("origin") + "/ResetPassword/";

				userService.forgotUserPassword(userProfile, baseUrl);
				responseMap.put(STATUS, SUCCESS);
				responseMap.put(MESSAGE,
						"Thanks! Please Check your email for further instructions to reset the password.");
				return new ResponseEntity<>(responseMap, HttpStatus.OK);
			}

		} catch (Exception exception) {
			logger.error("Exception in Forget Password: {}", exception.getMessage());
		}
		responseMap.put(STATUS, ERROR_STATUS);
		responseMap.put(MESSAGE, "Something went wrong.");
		return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
	}

	/**
	 * To set Password for user
	 * 
	 * @param token
	 * @param request
	 *            : To get http request header.
	 * @return
	 */
	@RequestMapping(value = { "/api/setPasswrd/{token}", "/api/resetPasswrd/{token}" })
	public ResponseEntity<Map<String, String>> setInitialPasswrd(@PathVariable String token,
			HttpServletRequest request) {
		Map<String, String> responseMap = new HashMap<>();
		try {
			User userProfile = null;
			if (request.getRequestURI().contains("resetPasswrd")) {
				userProfile = userService.getUserByPassword(token.replaceAll("~", "/").replaceAll("\\^", "."));
			} else {
				userProfile = userService.getUserByPassword(token);
			}
			if (userProfile != null) {
				if (userProfile.getUserAction() == 2) {
					if (request.getRequestURI().contains("resetPasswrd")) {
						responseMap.put(STATUS, SUCCESS);
						responseMap.put("userId", String.valueOf(userProfile.getId()));
						responseMap.put(MESSAGE, "Redirecting to set password page.");
						return new ResponseEntity<>(responseMap, HttpStatus.OK);
					} else {
						responseMap.put(STATUS, ERROR_STATUS);
						responseMap.put(MESSAGE, "Redirecting to login page.");
						return new ResponseEntity<>(responseMap, HttpStatus.OK);
					}
				}
				responseMap.put(STATUS, SUCCESS);
				responseMap.put("userId", String.valueOf(userProfile.getId()));
				responseMap.put(MESSAGE, "Redirecting to set password page.");
				return new ResponseEntity<>(responseMap, HttpStatus.OK);
			}
		} catch (Exception exception) {
			logger.error("Exception in Reseting Password: {}", exception.getMessage());
			responseMap.put(STATUS, ERROR_STATUS);
			responseMap.put(MESSAGE, exception.getMessage());
			return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
		}
		responseMap.put(STATUS, ERROR_STATUS);
		responseMap.put(MESSAGE, "Something went wrong.");
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	/**
	 * To set New Password for the user
	 * 
	 * @param userId:pass
	 *            the userId of use.
	 * @param key:Pass
	 *            the key to update new password
	 * @param request
	 *            : To get http request header.
	 * @return
	 */
	@RequestMapping(value = "/api/setNewPasswrd")
	public ResponseEntity<Map<String, String>> setNewPasswrd(Long userId, String key, HttpServletRequest request) {
		Map<String, String> responseMap = new HashMap<>();
		try {
			User user = userService.getUserById(userId);
			if (null == key) {
				responseMap.put(STATUS, ERROR_STATUS);
				responseMap.put(MESSAGE, env.getRequiredProperty("PASSWORD_REQUIRED"));
			} else if (key.length() < 8 && key.length() > 16) {
				responseMap.put(STATUS, ERROR_STATUS);
				responseMap.put(MESSAGE, env.getRequiredProperty("PASSWORD_LENGTH_ERROR"));
			} else if (!(key.matches("(.*[A-Z].*)") && key.matches("(.*[a-z].*)") && key.matches("(.*[0-9].*)")
					&& key.matches("(.*[!,@,#,$,%,^,&,*].*$)"))) {
				responseMap.put(STATUS, ERROR_STATUS);
				responseMap.put(MESSAGE, env.getRequiredProperty("PASSWORD_POLICY_ERROR"));
			} else {
				if (user != null) {
					user.setPassword(PasswordUtil.encryptPassword(key));
					user.setUserAction(0);
					userService.createEntity(User.class, user);
					responseMap.put(STATUS, SUCCESS);
					responseMap.put(MESSAGE, "Password Changed successfully. Redirecting to Login Page");
					
				}
			}
		} catch (Exception exception) {
			responseMap.put(STATUS, ERROR_STATUS);
			responseMap.put(MESSAGE, env.getRequiredProperty("SERVER_ERROR"));
			logger.error("Exception in Setting New Password: {}", exception.getMessage());
		}
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	/**
	 * To Change Password
	 * 
	 * @param userId:Pass
	 *            the userId
	 * @param currentPasswd:Pass
	 *            the Current Password
	 * @param newPasswd:Pass
	 *            The new Password
	 * @param request
	 *            : To get http request header.
	 * @return
	 */
	@PostMapping(value = "/api/changePasswd")
	public ResponseEntity<Map<String, String>> changePasswrd(Long userId, String currentPasswd, String newPasswd,
			HttpServletRequest request) {
		Map<String, String> responseMap = new HashMap<>();
		try {
			User userProfile = userService.getUserById(userId);

			if (userProfile != null) {
				if (!PasswordUtil.matchPassword(currentPasswd, userProfile.getPassword())) {
					responseMap.put(STATUS, ERROR_STATUS);
					responseMap.put(MESSAGE, "Current password mismatch");
					return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
				}

				if (null == newPasswd) {
					responseMap.put(STATUS, ERROR_STATUS);
					responseMap.put(MESSAGE, env.getRequiredProperty("PASSWORD_REQUIRED"));
					return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
				} else if (newPasswd.length() < 8 && newPasswd.length() > 16) {
					responseMap.put(STATUS, ERROR_STATUS);
					responseMap.put(MESSAGE, env.getRequiredProperty("PASSWORD_LENGTH_ERROR"));
					return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
				} else if (!(newPasswd.matches("(.*[A-Z].*)") && newPasswd.matches("(.*[a-z].*)")
						&& newPasswd.matches("(.*[0-9].*)") && newPasswd.matches("(.*[!,@,#,$,%,^,&,*].*$)"))) {
					responseMap.put(STATUS, ERROR_STATUS);
					responseMap.put(MESSAGE, env.getRequiredProperty("PASSWORD_POLICY_ERROR"));
					return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
				} else {
					userProfile.setPassword(PasswordUtil.encryptPassword(newPasswd));
					userService.createEntity(User.class, userProfile);

					responseMap.put(STATUS, SUCCESS);
					responseMap.put(MESSAGE, "Password Changed successfully.");
					
				}
			}
			return new ResponseEntity<>(responseMap, HttpStatus.OK);
		} catch (Exception e) {
			responseMap.put(STATUS, ERROR_STATUS);
			responseMap.put(MESSAGE, env.getRequiredProperty("SERVER_ERROR"));
			logger.error("Exception in Change Password: {}", e.getMessage());
			return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
