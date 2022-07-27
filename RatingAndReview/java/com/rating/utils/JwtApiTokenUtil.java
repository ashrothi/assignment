package com.rating.utils;

import java.io.Serializable;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.rating.bo.User;
import com.rating.business.logic.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author Ankita Shrothi
 */
@Component
//@PropertySource("file:${APP_PROPERTIES}")
//@PropertySource("file:${APP_PROPERTIES_OPTIMIZE}")
public class JwtApiTokenUtil implements Serializable {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	private Environment environment;
	
	@Autowired
	private UserService userService;

	private static final long serialVersionUID = -3301605592108950415L;

	private static final String CLAIM_KEY_USERNAME = "userName";
	private static final String CLAIM_KEY_AUDIENCE = "audience";

	public static final String ACCESS_TOKEN_VALIDITY_IN_MILLISECONDS = "access.token.validity";
	public static final String REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS = "refresh.token.validity";
	public static final String SINGLE_USER_SESSION = "single.user.session";
	private static final String CLAIM_KEY_USER_ID = "g10bec0nnec8api";
	/**
	 * Method used to generate token with time limit. For token using JWT concept.
	 * 
	 * @param user
	 * @return
	 */
	public String generateToken(User user) {
		long tokenValidity = Long.parseLong("1000");
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_KEY_USERNAME, user.getUserName());
		

		return Jwts.builder().setClaims(claims).setSubject(user.getUserName())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + tokenValidity))
				.setAudience(CLAIM_KEY_AUDIENCE)
				.signWith(SignatureAlgorithm.HS512,CLAIM_KEY_USER_ID)
				.compact();
	}

	/**
	 * Method used to generate refresh token with time limit.
	 * 
	 * @param user
	 * @return
	 */
	public String generateRefreshToken(User user) {
		long tokenValidity = Long.parseLong(environment.getProperty(REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS));
		if (StringUtils.isBlank(user.getUserName())) {
			throw new IllegalArgumentException("Cannot create JWT Token without username");
		}
		Claims claims = Jwts.claims().setSubject(user.getUserName());
		claims.put("scopes", "refresh_token");

		return Jwts.builder().setClaims(claims).setId(UUID.randomUUID().toString())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + tokenValidity))
				.signWith(SignatureAlgorithm.HS512,"g10bec0nnec8api")
				.compact();
	}

	/**
	 * Retrieve username from jwt token
	 * 
	 * @param token
	 * @return
	 */
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	/**
	 * get Token issue timestamp
	 * @param token
	 * @return
	 */
	public Date getIssuedAt(String token) {
		return getClaimFromToken(token, Claims::getIssuedAt);
	}
	
	/**Validates JWT token and returns User entity.
	 * @param token
	 * @return
	 */
	public User getValidUserFromJWT(String token) {
		Claims claims = getAllClaimsFromToken(token);
		
		String username = claims.getSubject();
		Date created = claims.getIssuedAt();
		Date expiry = claims.getExpiration();

		if(expiry.before(new Date()))
			return null;
		
		User user = userService.getUserByUserName(username);
		if(user == null)
			return null;
		


	
		
			
		return user;
	}
	
	public User matchValidUserFromJWT(String token,User userDetails) {
		//validate token and get user from it
		User tokenUser = getValidUserFromJWT(token);
		
		//if user from tokenUser is different then provided userDetails is not matching with token
		if(tokenUser == null || !tokenUser.getUserName().equals(userDetails.getUserName()))
			return null;
		
		return userDetails;
	}
	
	/**
	 * retrieve expiration date from jwt token
	 * 
	 * @param token
	 * @return
	 */
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	/**
	 * 
	 * @param token
	 * @param claimsResolver
	 * @return
	 */
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * for retrieveing any information from token we will need the secret key
	 * 
	 * @param token
	 * @return
	 */
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser()
				.setSigningKey("g10bec0nnec8api")
				.parseClaimsJws(token).getBody();
	}

	/**
	 * check if the token has expired
	 * 
	 * @param token
	 * @return
	 */
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	/**
	 * validate token
	 * 
	 * @param token
	 * @param userDetails
	 * @return
	 */
	public Boolean validateToken(String token, User userDetails) {
//		final String username = getUsernameFromToken(token);
//		return (username.equals(userDetails.getUserName()) && !isTokenExpired(token));
		 
		User validatedUser = matchValidUserFromJWT(token,userDetails);
		if(validatedUser != null)
			return true;
		
		return false;
	}

	/**
	 * get the Headers and Payload by decoding token.
	 * 
	 * @param token
	 */
	public void decodeToken(String token) {
		Decoder decoder = Base64.getUrlDecoder();
		String[] parts = token.split("\\.");
		String headers = new String(decoder.decode(parts[0]));
		String payload = new String(decoder.decode(parts[1]));
	}

	public String generateCustomToken(Map<String,Object> claims, long expirationTime) {

		return Jwts.builder().setClaims(claims)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + "10000"))
				.signWith(SignatureAlgorithm.HS512,"g10bec0nnec8api")
				.compact();

	}
}