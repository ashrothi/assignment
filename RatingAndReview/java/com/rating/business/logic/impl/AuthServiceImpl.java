package com.rating.business.logic.impl;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rating.dl.CrudRepository;
import com.rating.dto.ApiResponse;
import com.rating.utils.JwtTokenUtil;
import com.rating.utils.PasswordUtil;
import com.rating.bo.ApiUser;
import com.rating.business.logic.ApiUserService;
import com.rating.business.logic.AuthService;
import com.rating.business.logic.ServiceCommonUtils;

/**
 * @author Ankita Shrothi
 *
 */
@Service
@Transactional("RatingAndReviewTransactionManager")
public class AuthServiceImpl extends CrudServiceImpl implements AuthService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ApiUserService apiUserService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private CrudRepository crudRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rating.bl.AuthService#getToken(java.util.Map) case:Get Token for
	 * External API endPointUrl:/gcapi/auth
	 */
	@Override
	public ApiResponse getToken(Map<?, ?> params) {
		String requestId = "GCAPI" + ServiceCommonUtils.generateUniqueIdByDateTime();
		String userName = (String) params.get("username");
		String password = (String) params.get("password");

//		if (params == null || params.size() == 0) {
//			return new ApiResponse("api.request.body.empty");
//		}
		if (userName == null || userName.isEmpty() || password == null || password.isEmpty()) {

			return new ApiResponse("api.auth.param.missing");
		}

		try {
			System.out.println(params);
			ApiUser user = apiUserService.getUserByUserName(userName);
			System.out.println(user.toString());
			if (user == null || !PasswordUtil.matchPassword(password, user.getPassword())) {

				return new ApiResponse("api.invalid.credential");
			}
			if (user.isDeleted() || user.isLocked()) {

				return new ApiResponse("api.user.or.account.locked");
			}
			String token = null;
			if (user.getToken() == null || user.getTokenExpiryDate() == null
					|| user.getTokenExpiryDate().before(new Date())) {
				token = jwtTokenUtil.generateToken(user);
				user.setToken(token);
				user.setTokenExpiryDate(DateUtils.addHours(new Date(), 4));
				crudRepository.mergeEntity(ApiUser.class, user);
			} else {
				token = user.getToken();
			}
			// Entry in AuditLogApiTransaction

			ApiResponse response = new ApiResponse();
			response.setHttpStatus(HttpStatus.OK.value());
			response.setToken(token);
			return response;
		} catch (Exception e) {

			logger.error("Exception in Getting Token: {}", e.getMessage());
			return new ApiResponse("api.internal.server.error");
		}
	}
}
