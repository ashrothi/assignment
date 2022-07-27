package com.rating.business.logic;

import java.util.Date;

/**
 * @author Ankita Shrothi
 *
 */
public interface TokenManagerService extends CrudService {

	public void insertBlacklistedToken(String token, Date expiryDate);

	public Boolean isTokenBlacklisted(String token);

	public void deleteTokens();
}
