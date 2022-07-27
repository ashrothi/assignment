package com.rating.business.logic.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rating.dl.CrudRepository;
import com.rating.dl.Repository;
import com.rating.bo.TokenManager;
import com.rating.business.logic.TokenManagerService;

/**
 * @author Ankita Shrothi
 *
 */
@Service
@Transactional("RatingAndReviewTransactionManager")
public class TokenManagerServiceImpl extends CrudServiceImpl implements TokenManagerService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CrudRepository crudRepository;

	@Override
	public void insertBlacklistedToken(String token, Date expiryDate) {
		TokenManager removedToken = new TokenManager();
		try {
			logger.debug("Inserting new blacklisted token: [{}]", token);
			
			removedToken.setTokenValue(token);
			removedToken.setExpiryDate(expiryDate);
			crudRepository.createEntity(TokenManager.class, removedToken);
		} catch (Exception e) {
			logger.error("Exception while API User creation:{}", e.getMessage());
		}
	}

	@Override
	public Boolean isTokenBlacklisted(String token) {
		return null != getTokenDetails(token);
	}

	private TokenManager getTokenDetails(String token) {
		try {
			return crudRepository.getSingleEntity(CrudRepository.QueryFilter.newInstance(TokenManager.class)
					.addQueryAttribute("tokenValue", token, Repository.QueryMode.EQ, String.class));
		} catch (Exception exception) {
			logger.error("Exception while API User creation:{}", exception.getMessage());
			return null;
		}
	}
	
	@Override
	public void deleteTokens() {
		logger.info("Started task for removing expired tokens.");
		try {
			List<TokenManager> tokens = getAllTokens();
			for (TokenManager token : tokens) {
				if (token.getExpiryDate().before(new Date())) {
					crudRepository.deleteEntity(TokenManager.class, token.getId());
				}
			}
		} catch (Exception e) {
			logger.error("Unable to delete the tokens : {}", e.getMessage());
		}
	}
	
	private List<TokenManager> getAllTokens() {
		CrudRepository.QueryFilter<TokenManager> queryFilter = CrudRepository.QueryFilter.newInstance(TokenManager.class);
		return crudRepository.getEntityEntries(queryFilter);
	}
}
