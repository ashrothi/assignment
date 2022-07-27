package com.rating.react.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.context.Context;

import com.rating.exc.RatingAndReviewException;
import com.rating.utils.JwtApiTokenUtil;
import com.rating.utils.PasswordUtil;
import com.rating.business.logic.TokenManagerService;
import com.rating.business.logic.UserService;

import io.jsonwebtoken.ExpiredJwtException;

@Controller

@Transactional(value = "RatingAndReviewTransactionManager")
public class SignInController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserService userService;

	@Resource
	private Environment env;

	@Autowired
	private JwtApiTokenUtil jwtTokenUtil;

	@Autowired
	private TokenManagerService tokenManagerService;

	@Autowired
	@Qualifier("authenticationManager")
	protected AuthenticationManager authenticationManager;

	public static final String MAX_WRONG_ATTEMPTS_NUMBER = "max.wrong.attempts";

	/**
	 * This method will validate first level of user and password check. Based on
	 * equality an OTP will be generated
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@PostMapping(value = "/api/validate/user")
	public ResponseEntity<Map<String, Object>> validateUser(@RequestParam String username,
			@RequestParam String password, HttpServletRequest request) throws MessagingException, IOException {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			int maxWrongAttempts = Integer.parseInt(env.getProperty(MAX_WRONG_ATTEMPTS_NUMBER));
			com.rating.bo.User user = userService.getUserByUserNameOrEmail(username);
			responseMap.put("sessionId", request.getSession().getId());
			if (user == null) {
				responseMap.put("status", "failure");
				responseMap.put("errorMessage", "Invalid Username & Password");
			} else if (user.isLocked()) {
				responseMap.put("status", "failure");
				responseMap.put("errorMessage", "User is locked");

			} else if (!PasswordUtil.matchPassword(password, user.getPassword())) {
				System.out.println(user.getPassword());
				responseMap.put("status", "failure");
				Integer wrongAttempts = user.getAttempts() + 1;
				user.setAttempts(wrongAttempts);
				if (wrongAttempts == maxWrongAttempts) {
					user.setLocked(true);
					responseMap.put("errorMessage", "User has been locked due to too many failed login attempts");
				} else {
					int attemptsLeft = maxWrongAttempts - wrongAttempts;
					responseMap.put("errorMessage", "Invalid Password. You have " + attemptsLeft
							+ " attempt(s) before your account is locked.");
				}
				userService.updateUser(user);

			} else {
				responseMap.put("status", "success");
				responseMap.put("userId", String.valueOf(user.getId()));
				responseMap.put("token", jwtTokenUtil.generateToken(user));
				responseMap.put("refreshToken", jwtTokenUtil.generateRefreshToken(user));

				int randomInt = (int) Math.round(Math.random() * (999999 - 100000 + 1) + 100000);
				String secCode = Integer.toString(randomInt);
				user.setSecurityKey(secCode);
				user.setAttempts(0);
				user.setLastActive(new Date());
				userService.updateUser(user);

				responseMap.put("timeout", "10000");

				Context context = new Context();
				context.setVariable("userName", user.getUserName());
				context.setVariable("securityCode", secCode);

				responseMap.put("status", "otp-not-required");

			}
		} catch (RatingAndReviewException e) {
			responseMap.put("status", "failure");
			responseMap.put("errorMessage", e.getMessage());

		}
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	/**
	 * To logout user
	 * 
	 * @param refreshToken:Pass refresh token to logout
	 * @param request           : To get http request header.
	 * @param response          : To return http response header.
	 * @return
	 */
	@GetMapping(value = "/api/logout")
	public ResponseEntity<String> logoutPage(@RequestParam(value = "refreshToken", required = true) String refreshToken,
			HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			Date tokenExpireTime = new Date();
			tokenExpireTime = DateUtils.addMinutes(tokenExpireTime, 10);
			String authToken = request.getHeader("Authorization");
			if (authToken.startsWith("Bearer ")) {
				authToken = authToken.replace("Bearer ", "");
			}
			tokenManagerService.insertBlacklistedToken(authToken, tokenExpireTime);

			Date rtExpireTime = new Date();
			rtExpireTime = DateUtils.addMinutes(rtExpireTime, 30);
			tokenManagerService.insertBlacklistedToken(refreshToken, rtExpireTime);

			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return new ResponseEntity<>("User logged out successfully.", HttpStatus.OK);
	}

	/**
	 * This method will validate refresh token and provide a new valid access token
	 * and refresh token with new expiration time.
	 * 
	 * @param refreshToken
	 * @return
	 * @throws RatingAndReviewException
	 */
	@PostMapping(value = "/api/refresh/token")
	public ResponseEntity<Map<String, Object>> refreshToken(@RequestParam String refreshToken,
			HttpServletRequest request, HttpServletResponse response) throws RatingAndReviewException {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			if (tokenManagerService.isTokenBlacklisted(refreshToken)) {
				responseMap.put("status", "failure");
				responseMap.put("errorCode", 403);
				responseMap.put("errorMessage", "Token invalid or expired");
				return new ResponseEntity<>(responseMap, HttpStatus.FORBIDDEN);
			}
			String userName = jwtTokenUtil.getUsernameFromToken(refreshToken);
			com.rating.bo.User user = userService.getUserByUserName(userName);

			if (user == null) {

				responseMap.put("status", "failure");
				responseMap.put("errorCode", 403);
				responseMap.put("errorMessage", "User not exist");
				return new ResponseEntity<>(responseMap, HttpStatus.FORBIDDEN);
			}

			// if refresh token is valid return new access token.
			if (jwtTokenUtil.validateToken(refreshToken, user)) {
				responseMap.put("status", "success");
				responseMap.put("token", jwtTokenUtil.generateToken(user));
				responseMap.put("refreshToken", jwtTokenUtil.generateRefreshToken(user));
			}
		} catch (ExpiredJwtException e) {
			com.rating.bo.User user = userService.getUserByUserNameOrEmail(e.getClaims().getSubject());

			responseMap.put("errorCode", 403);
			responseMap.put("errorMessage", "Session Expired.");
			return new ResponseEntity<>(responseMap, HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	/**
	 * returns success if OTP matches
	 * 
	 * @return
	 */
	@PostMapping("/api/isLoginSuccess")
	public ResponseEntity<Map<String, Object>> isLoginSuccess(String userName, String otp, HttpServletRequest request) {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			com.rating.bo.User userProf = userService.getUserByUserNameOrEmail(userName);
			if (userProf.getSecurityKey().equals(otp)) {
				int maxAge = 10;
				responseMap.put("status", "otp-success");
				responseMap.put("maxAge", maxAge);
			} else {
				responseMap.put("status", "otp-failure");
			}
		} catch (Exception exception) {
			responseMap.put("status", "otp-failure");
			logger.error("Exception: {}", exception.getMessage());
		}
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	/**
	 * This method is used for logging into portal from other portals by taking the
	 * credentials and page from the URL.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/auto/login")
	public ResponseEntity<Map<String, Object>> autoLogin(HttpServletRequest request) {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			// http://i.teramatrix.in:7171/autoLogin?j_page=index&j_username=H35mUBNsPCLX3I90YgGCjg==&j_password=H35mUBNsPCLX3I90YgGCjg==
			String username = PasswordUtil.decrypt(request.getParameter("j_username").replace(' ', '+'));
			String password = PasswordUtil.decrypt(request.getParameter("j_password").replace(' ', '+'));

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password,
					null);

			authToken.setDetails(new WebAuthenticationDetails(request));
			authenticationManager.authenticate(authToken);

			com.rating.bo.User user = userService.getUserByUserNameOrEmail(username);
			responseMap.put("status", "success");
			responseMap.put("userId", String.valueOf(user.getId()));
			responseMap.put("token", jwtTokenUtil.generateToken(user));
			responseMap.put("refreshToken", jwtTokenUtil.generateRefreshToken(user));

		} catch (Exception exception) {
			if (exception instanceof BadCredentialsException) {
				responseMap.put("status", "failure");
				responseMap.put("message", "Bad Credentials");
			}
			if (exception instanceof AuthenticationException) {
				responseMap.put("status", "failure");
				responseMap.put("message", "Authentication Fails");
			}
			responseMap.put("status", "failure");
			return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}
}
