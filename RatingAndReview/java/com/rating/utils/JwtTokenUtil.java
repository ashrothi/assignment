package com.rating.utils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.rating.bo.ApiUser;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author Ankita Shrothi The Class JwtTokenUtil.
 */
@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = -3301605591108950415L;

	private static final String CLAIM_KEY_USERNAME = "sub";
	private static final String CLAIM_KEY_AUDIENCE = "audience";
	private static final String CLAIM_KEY_CREATED = "created";
	private static final String CLAIM_KEY_USER_ID = "userId";


	/**
	 * Method used to generate token with time limit. For token using JWT concept.
	 * This method for api user.
	 * 
	 * @param user
	 * @return
	 */
	public String generateToken(ApiUser user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_KEY_USERNAME, user.getUserName());
		claims.put(CLAIM_KEY_USER_ID, user.getId());
		claims.put(CLAIM_KEY_AUDIENCE, "web");
		claims.put(CLAIM_KEY_CREATED, new Date());
		return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512,"g10bec0nnec8api").compact();
	}
}