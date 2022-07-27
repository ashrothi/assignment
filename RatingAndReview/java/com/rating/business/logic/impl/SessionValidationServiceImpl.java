package com.rating.business.logic.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rating.utils.JwtApiTokenUtil;
import com.rating.utils.PasswordUtil;
import com.rating.bo.User;
import com.rating.business.logic.SessionValidationService;
import com.rating.business.logic.TokenManagerService;
import com.rating.business.logic.UserService;

import io.jsonwebtoken.ExpiredJwtException;

@Service
@Transactional("RatingAndReviewTransactionManager")
public class SessionValidationServiceImpl implements SessionValidationService {

	@Autowired
	private UserService userService;

	@Autowired
	private JwtApiTokenUtil jwtTokenUtil;

	@Autowired
	private TokenManagerService tokenManagerService;


	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Boolean validateAuthToken(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws IOException {
		String authToken = request.getHeader("Authorization");
		String accountId = request.getHeader("accountId");

		String userId = request.getHeader("userId");
		User user = null;
		if (authToken.startsWith("Basic ")) {
			authToken = authToken.replace("Basic ", "");
			String[] credentials = decode(authToken);
			if (credentials == null || credentials.length < 2) {
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				
				
				return false;
			}

			String userName = credentials[0];
			String password = credentials[1];
			user = userService.getUserByUserName(userName);

			if (user == null || !PasswordUtil.matchPassword(password, user.getPassword())) {
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				
				return false;
			}
		} else {
			authToken = authToken.replace("Bearer ", "");
			String userName = null;
			if (tokenManagerService.isTokenBlacklisted(authToken)) {
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				
				return false;
			}

			try {
				userName = jwtTokenUtil.getUsernameFromToken(authToken);
			} catch (IllegalArgumentException e) {
				logger.info("Unable to get JWT Token");
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				
				return false;
			} catch (ExpiredJwtException e) {
				logger.info("JWT Token has expired");
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				
				return false;
			}

			user = userService.getUserByUserName(userName);

			if (user == null) {
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return false;
			}

			if (StringUtils.isNotBlank(userId) && !StringUtils.equalsIgnoreCase(user.getId().toString(), userId)) {
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				
				return false;
			}
		}

		if (user.isDeleted()
				) {
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);

			
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			
			return false;
		}

		// if token is valid configure Spring Security to manually set
		// authentication
		if (jwtTokenUtil.validateToken(authToken, user)) {
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
					user, null);
			usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			// After setting the Authentication in the context, we specify
			// that the current user is authenticated. So it passes the
			// Spring Security Configurations successfully.
			SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			return true;
		}
		return null;
	}

	@Override
	public String[] decode(final String encoded) {
		try {
			final byte[] decodedBytes = Base64.decodeBase64(encoded.getBytes());
			final String pair = new String(decodedBytes);
			return pair.split(":", 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public byte[] restResponseBytes(String error_code, String message) throws IOException {

		Map<String, Object> response = new HashMap<>();
		response.put("errorCode", error_code);
		response.put("errorMessage", message);
		String serialized = new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(response);
		return serialized.getBytes();
	}

	
}
