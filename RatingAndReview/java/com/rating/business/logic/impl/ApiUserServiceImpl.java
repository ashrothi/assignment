package com.rating.business.logic.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rating.dl.CrudRepository;
import com.rating.dl.QueryFilterTemplate;
import com.rating.dl.Repository;
import com.rating.dl.Repository.QueryMode;
import com.rating.dl.Repository.SortAttribute.SortMode;
import com.rating.dto.ApiResponse;
import com.rating.dto.ReactApiUserDto;
import com.rating.exc.RatingAndReviewException;
import com.rating.utils.JwtTokenUtil;
import com.rating.utils.PasswordUtil;
import com.rating.utils.QueryUtils;
import com.rating.utils.order.OrderParams;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.rating.bo.ApiUser;
import com.rating.bo.User;
import com.rating.business.logic.ApiUserService;
import com.rating.business.logic.CommonService;
import com.rating.business.logic.ServiceCommonUtils;
import com.rating.utils.search.PaginationSearchParams;
import com.rating.utils.search.PaginationSearchResult;

/**
 * @author Ankita Shrothi
 *
 */

/***
 * This class is used to create API user ,Update API User and Get Details API users Also used for Generate
 * Password,change Password,forget Password
 *
 */
@Service
@Transactional("RatingAndReviewTransactionManager")
public class ApiUserServiceImpl extends CrudServiceImpl implements ApiUserService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CrudRepository crudRepository;

	@Autowired
	private Gson gson;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Resource
	private Environment env;

	@Autowired
	private Validator validator;

	@Autowired(required = true)
	private CommonService commonService;

	/**
	 * @see com.rating.business.logic.ApiUserService#getUsers(java.util.Map) This
	 *      API is used for getting the API User Family Tree. Case:-Get API User
	 *      Family Tree details used by External API EndPointUrl:/gcapi/users
	 *      HTTP_METHOD:GET
	 * @see /RatingAndReview/src/java/com/rating/bo/ApiMapping for EndPoint and
	 *      HTTP_METHOD
	 */

	@SuppressWarnings("unchecked")
	@Override
	public ApiResponse getUsers(Map<String, Object> params) {

		params.get("loggedInAccountId");

		ServiceCommonUtils.generateUniqueIdByDateTime();
		getUserByUserName(
				params.get("loggedInUserName") != null ? params.get("loggedInUserName").toString() : "gtadmin");

		Repository.ComplexQueryAttribute complexQueryAttribute = Repository.ComplexQueryAttribute
				.newInstance(Repository.ComplexQueryMode.OR);

		CrudRepository.QueryFilter<ApiUser> queryFilter = CrudRepository.QueryFilter.newInstance(ApiUser.class);

		queryFilter = QueryUtils.buildPagingAndSortingFilter(queryFilter, params, ApiUser.class);
		queryFilter.addComplexQueryAttribute(complexQueryAttribute);
		// queryFilter.addQueryAttribute("locked", false, QueryMode.EQ, boolean.class);
		// queryFilter.addQueryAttribute("deleted", false, QueryMode.EQ, boolean.class);
		List<ApiUser> users = crudRepository.getEntityEntries(queryFilter);
		return new ApiResponse(HttpStatus.OK.value(), users, crudRepository.countEntityEntries(queryFilter).intValue(),
				users.size());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rating.bl.ApiUserService#getUserByToken(java.lang.String)
	 */
	@Override
	public ApiUser getUserByToken(String token) {
		ApiUser user = crudRepository.getSingleEntity(CrudRepository.QueryFilter.newInstance(ApiUser.class)
				.addQueryAttribute("token", token, Repository.QueryMode.EQ, String.class));

		return user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rating.bl.ApiUserService#getUserByUserName(java.lang.String)
	 */
	@Override
	public ApiUser getUserByUserName(String userName) {
		System.out.println(userName);
		ApiUser user = crudRepository.getSingleEntity(CrudRepository.QueryFilter.newInstance(ApiUser.class)
				.addQueryAttribute("userName", userName, Repository.QueryMode.EQ, String.class));
		System.out.println("user " + user);
		return user;
	}

	/**
	 * @see com.rating.business.logic.ApiUserService#getUser(java.util.Map) This API
	 *      is used for getting the User details on the basis of id.
	 * 
	 * @param id : Here pass the id for which you need User details.
	 * @see /RatingAndReview/src/java/com/rating/bo/ApiMapping for EndPoint and
	 *      HTTP_METHOD Case:-Get User details on the basis of id used by External
	 *      API EndPoint:/gcapi/user/{id} HTTP_METHOD:GET
	 */
	@Override
	public ApiResponse getUser(Map<String, Object> params) {
		Long userId = Long.parseLong(params.get("id").toString());
		ApiUser user = crudRepository.getEntityById(ApiUser.class, userId);
		params.get("loggedInAccountId");

		if (user == null) {
			logger.error("User not found : " + params);
			return new ApiResponse("api.user.not.found");
		}
		return new ApiResponse(HttpStatus.OK.value(), user);
	}

	/**
	 * @see com.rating.business.logic.ApiUserService#createOrUpdateUser(java.util.Map)
	 *      This method is used to Create or Update API User case:-used to Create or
	 *      Update API User EndPoint:/gcapi/user HTTP_METHOD:POST
	 * @see /RatingAndReview/src/java/com/rating/bo/ApiMapping for EndPoint and
	 *      HTTP_METHOD
	 */
	@Override
	public ApiResponse createOrUpdateUser(Map<String, Object> params) {

		ServiceCommonUtils.generateUniqueIdByDateTime();
		getUserByUserName(
				params.get("loggedInUserName") != null ? params.get("loggedInUserName").toString() : "gtadmin");

		logger.error("Update user  : " + params);
		if (params == null || params.size() == 0) {
			return new ApiResponse("api.request.body.empty");
		}
		try {
			JsonElement jsonElement = gson.toJsonTree(params);
			ApiUser user = gson.fromJson(jsonElement, ApiUser.class);
			params.get("loggedInAccountId");

			if (user.getId() == null) {

				if (getUserByUserNameOrEmail(user.getEmail()) != null) {

					return new ApiResponse("api.user.email.exist");
				}
				if (getUserByUserNameOrEmail(user.getUserName()) != null) {

					return new ApiResponse("api.user.username.exist");
				}

				Set<ConstraintViolation<ApiUser>> constraintViolations = validator.validate(user);
				if (constraintViolations.size() == 0) {
					user.setLocked(true);
					// user.setDeleted(true);
					// user.setDeleteDate(new Date());
					user.setPassword(PasswordUtil.generatePassword());
					crudRepository.createEntity(ApiUser.class, user);

					return new ApiResponse(HttpStatus.CREATED.value());
				} else {
					List<String> errorMessages = new ArrayList<String>();
					for (ConstraintViolation<ApiUser> constraintViolation : constraintViolations) {
						errorMessages.add(constraintViolation.getMessage());
					}

					return new ApiResponse(HttpStatus.BAD_REQUEST.value(), StringUtils.join(errorMessages, ","));
				}

			} else {
				getEntityById(ApiUser.class, user.getId());

				Set<ConstraintViolation<ApiUser>> constraintViolations = validator.validate(user);
				if (constraintViolations.size() == 0) {
					crudRepository.mergeEntity(ApiUser.class, user);
					return new ApiResponse(HttpStatus.NO_CONTENT.value());
				} else {
					List<String> errorMessages = new ArrayList<String>();
					for (ConstraintViolation<ApiUser> constraintViolation : constraintViolations) {
						errorMessages.add(constraintViolation.getMessage());
					}
					return new ApiResponse(HttpStatus.BAD_REQUEST.value(), StringUtils.join(errorMessages, ","));
				}
			}
		} catch (Exception e) {
			logger.error("Unable to create the user : " + e.getMessage());
			return new ApiResponse("api.user.update.fail");

		}

	}

	/**
	 * @see com.rating.business.logic.ApiUserService#deleteUser(java.util.Map) This
	 *      method is used Delete API User on basis of id case:-used to Delete API
	 *      User
	 * @param id EndPointUrl:/gcapi/user/{id} HTTP_METHOD:DELETE
	 * @see /RatingAndReview/src/java/com/rating/bo/ApiMapping for EndPoint and
	 *      HTTP_METHOD
	 */
	@Override
	public ApiResponse deleteUser(Map<String, Object> params) {

		Long userId = Long.parseLong(params.get("id").toString());
		params.get("loggedInAccountId");
		Long loggedInUserId = (Long) params.get("loggedInUserId");

		if (userId == loggedInUserId) {
			return new ApiResponse("api.user.self.delete");
		}

		ApiUser user = crudRepository.getEntityById(ApiUser.class, userId);

		if (user == null) {
			logger.error("User not found : " + params);
			return new ApiResponse("api.user.not.found");
		}
		if (user.isDeleted()) {
			logger.error("User is already deleted : " + params);
			return new ApiResponse("api.user.already.deleted");
		}
		try {
			user.setDeleted(true);
			user.setDeleteDate(new Date());
			crudRepository.mergeEntity(ApiUser.class, user);
		} catch (Exception e) {
			logger.error("Unable to delete the user : " + e.getMessage());
			return new ApiResponse("api.user.delete.fail");
		}
		return new ApiResponse(HttpStatus.NO_CONTENT.value());
	}

	/**
	 * @see com.rating.business.logic.ApiUserService#suspendUser(java.util.Map) This
	 *      method is used to Suspend API User on basis of id
	 *      EndPoint:/gcapi/user/suspend/{id} HTTP_METHOD:PUT
	 * @param id:Here pass the id for which you need to suspend API User .
	 * @see /RatingAndReview/src/java/com/rating/bo/ApiMapping for EndPoint and
	 *      HTTP_METHOD
	 */
	@Override
	public ApiResponse suspendUser(Map<String, Object> params) {
		Long userId = Long.parseLong(params.get("id").toString());
		params.get("loggedInAccountId");
		Long loggedInUserId = (Long) params.get("loggedInUserId");

		if (userId == loggedInUserId) {
			return new ApiResponse("api.user.self.suspend");
		}

		ApiUser user = crudRepository.getEntityById(ApiUser.class, userId);
		if (user == null) {
			logger.error("User not found : " + params);
			return new ApiResponse("api.user.not.found");
		}
		if (user.isLocked()) {
			logger.error("User is already suspended : " + params);
			return new ApiResponse("api.user.already.suspended");
		}
		try {
			user.setLocked(true);
			// user.setToken(jwtTokenUtil.generateToken(user));
			crudRepository.mergeEntity(ApiUser.class, user);
		} catch (Exception e) {
			logger.error("Unable to suspend the user : " + e.getMessage());
			return new ApiResponse("api.user.suspend.fail");
		}
		return new ApiResponse(HttpStatus.NO_CONTENT.value());
	}

	/**
	 * @see com.rating.business.logic.ApiUserService#resumeUser(java.util.Map) This
	 *      method is used to Resume API User on basis of id
	 *      EndPoint:/gcapi/user/resume/{id} HTTP_METHOD:PUT
	 * @param id:Here pass the id for which you need to resume API User .
	 * @see /RatingAndReview/src/java/com/rating/bo/ApiMapping for EndPoint and
	 *      HTTP_METHOD
	 */
	@Override
	public ApiResponse resumeUser(Map<String, Object> params) {
		Long userId = Long.parseLong(params.get("id").toString());
		params.get("loggedInAccountId");
		ApiUser user = crudRepository.getEntityById(ApiUser.class, userId);

		if (user == null) {
			logger.error("User not found : " + params);
			return new ApiResponse("api.user.not.found");
		}
		if (!user.isLocked()) {
			logger.error("User is already resumed : " + params);
			return new ApiResponse("api.user.already.resumed");
		}
		try {
			user.setLocked(false);
			crudRepository.mergeEntity(ApiUser.class, user);
		} catch (Exception e) {
			logger.error("Unable to resume the user : " + e.getMessage());
			return new ApiResponse("api.user.resume.fail");
		}
		return new ApiResponse(HttpStatus.NO_CONTENT.value());
	}

	/**
	 * @see com.rating.business.logic.ApiUserService#forgotPassword(java.util.Map)
	 *      This method is used when API User forget password param:userName,email
	 *      EndPoint:/gcapi/forgotPassword/{userName}/{email} HTTP_METHOD:PUT
	 * @see /RatingAndReview/src/java/com/rating/bo/ApiMapping for EndPoint and
	 *      HTTP_METHOD
	 */
	@Override
	public ApiResponse forgotPassword(Map<String, Object> params) {
		String email = (String) params.get("email");
		String userName = (String) params.get("userName");
		if (email == null || email.isEmpty()) {
			return new ApiResponse("api.email.missing");
		}
		if (userName == null || userName.isEmpty()) {
			return new ApiResponse("api.username.missing");
		}
		try {
			CrudRepository.QueryFilter<ApiUser> queryFilter = CrudRepository.QueryFilter.newInstance(ApiUser.class);
			queryFilter.addQueryAttribute("userName", userName, QueryMode.EQ, String.class);
			queryFilter.addQueryAttribute("email", email, QueryMode.EQ, String.class);

			ApiUser user = crudRepository.getSingleEntity(queryFilter);

			if (user == null) {
				logger.error("User not found : " + params);
				return new ApiResponse("api.user.not.found");
			}
			user.setPassword(PasswordUtil.generatePassword());
			user.setToken(jwtTokenUtil.generateToken(user));
			crudRepository.mergeEntity(ApiUser.class, user);
		} catch (Exception e) {
			logger.error("Unable to send reset password link : " + e.getMessage());
			return new ApiResponse("api.send.reset.password.link.fail");
		}
		return new ApiResponse("api.send.reset.password.link.success");
	}

	/**
	 * @see com.rating.business.logic.ApiUserService#changePassword(java.util.Map)
	 *      This method is used when API User need to change Password
	 *      param:currentPassword,newPassword
	 *      EndPoint:/gcapi/changePassword/{currentPassword}/{newPassword}
	 *      HTTP_METHOD:PUT
	 * @see /RatingAndReview/src/java/com/rating/bo/ApiMapping for EndPoint and
	 *      HTTP_METHOD
	 */
	@Override
	public ApiResponse changePassword(Map<String, Object> params) {

		ServiceCommonUtils.generateUniqueIdByDateTime();
		ApiUser user = getUserByUserName(
				params.get("loggedInUserName") != null ? params.get("loggedInUserName").toString() : "gtadmin");

		// Long loggedInUserId = (Long) params.get("loggedInUserId");
		// ApiUser user = crudRepository.getEntityById(ApiUser.class, loggedInUserId);

		String currentPassword = (String) params.get("currentPassword");
		String newPassword = (String) params.get("newPassword");

		if (!PasswordUtil.matchPassword(currentPassword, user.getPassword())) {

			return new ApiResponse("api.current.password.incorrect");
		}
		try {
			user.setPassword(PasswordUtil.encryptPassword(newPassword));
			user.setToken(jwtTokenUtil.generateToken(user));
			crudRepository.mergeEntity(ApiUser.class, user);
		} catch (Exception e) {
			logger.error("Unable to send reset password link : " + e.getMessage());

			return new ApiResponse("api.change.password.fail");
		}

		return new ApiResponse("api.change.password.success");
	}

	/**
	 * @see com.rating.business.logic.ApiUserService#generatePassword(java.util.Map)
	 *      This method is used when API User need to generate Password param:key
	 *      EndPoint:/gcapi/generatePassword/{key} HTTP_METHOD:GET
	 * @see /RatingAndReview/src/java/com/rating/bo/ApiMapping for EndPoint and
	 *      HTTP_METHOD
	 */
	@Override
	public ApiResponse generatePassword(Map<String, Object> params) {
		String key = (String) params.get("key");
		ApiUser user = crudRepository.getSingleEntity(CrudRepository.QueryFilter.newInstance(ApiUser.class)
				.addQueryAttribute("password", key, Repository.QueryMode.EQ, String.class));

		if (user == null) {
			return new ApiResponse("api.generate.password.link.invalid");
		}
		try {
			String tempPassword = RandomStringUtils.randomAlphanumeric(6);
			user.setLocked(false);
			// user.setDeleted(false);
			// user.setDeleteDate(null);
			user.setToken(jwtTokenUtil.generateToken(user));
			user.setPassword(PasswordUtil.encryptPassword(tempPassword));
			crudRepository.mergeEntity(ApiUser.class, user);
		} catch (Exception e) {
			logger.error("Unable to send reset password link : " + e.getMessage());
			return new ApiResponse("api.send.reset.password.link.fail");
		}
		return new ApiResponse("api.password.sent");
	}

	private ApiUser getUserByUserNameOrEmail(String userNameOrEmailId) {
		Repository.ComplexQueryAttribute complexQueryAttribute = Repository.ComplexQueryAttribute
				.newInstance(Repository.ComplexQueryMode.OR);
		complexQueryAttribute.addQueryAttribute(Repository.QueryAttribute.newInstance("userName", userNameOrEmailId,
				Repository.QueryMode.ILIKE, String.class));
		complexQueryAttribute.addQueryAttribute(Repository.QueryAttribute.newInstance("email", userNameOrEmailId,
				Repository.QueryMode.ILIKE, String.class));
		return crudRepository.getSingleEntity(
				CrudRepository.QueryFilter.newInstance(ApiUser.class).addComplexQueryAttribute(complexQueryAttribute));
	}

	public PaginationSearchResult findAllApiUserForDataTable(PaginationSearchParams paginationSearchParams,
			final OrderParams orderParams, final User userProfile) {

		return new QueryFilterTemplate<PaginationSearchParams>(this.crudRepository, paginationSearchParams, null) {

			public CrudRepository.QueryFilter buildSearchFilter(PaginationSearchParams searchContainer) {

				Repository.ComplexQueryAttribute complexQueryAttribute = Repository.ComplexQueryAttribute
						.newInstance(Repository.ComplexQueryMode.OR);

				CrudRepository.QueryFilter<ApiUser> queryFilter = CrudRepository.QueryFilter.newInstance(ApiUser.class);

				queryFilter.addPagingParams(searchContainer.getPage(), searchContainer.getPageSize())
						.addComplexQueryAttribute(complexQueryAttribute);

				/*
				 * Added for sorting on specific fields
				 */
				if (orderParams.getOrderParameters().size() > 0) {
					for (Entry<String, String> entry : orderParams.getOrderParameters().entrySet()) {
						if (entry.getValue().equals("asc")) {
							queryFilter.addSortAttribute(entry.getKey(), SortMode.ASC);
						} else {
							queryFilter.addSortAttribute(entry.getKey(), SortMode.DESC);
						}
					}
				} else {
					queryFilter.addSortAttribute("id", SortMode.DESC);
				}

				if (searchContainer.getSearchValue() != null && searchContainer.getSearchValue().length() > 0) {
					try {
						Repository.ComplexQueryAttribute searchQueryAttribute = commonService.searchTableData("ApiUser",
								searchContainer.getSearchValue());
						queryFilter.addComplexQueryAttribute(searchQueryAttribute);
					} catch (RatingAndReviewException e) {
						logger.error("Error while getting API Users : {}", ExceptionUtils.getStackTrace(e));
					}
				}
				return queryFilter;
			}
		}.load(ApiUser.class);
	}

	@Override
	public ApiUser getUserById(long id) {
		Repository.ComplexQueryAttribute complexQueryAttribute = Repository.ComplexQueryAttribute
				.newInstance(Repository.ComplexQueryMode.OR);
		complexQueryAttribute.addQueryAttribute(
				Repository.QueryAttribute.newInstance("id", id, Repository.QueryMode.EQ, Long.class));
		return crudRepository.getSingleEntity(
				CrudRepository.QueryFilter.newInstance(ApiUser.class).addComplexQueryAttribute(complexQueryAttribute));
	}

	@Override
	public ApiUser addApiUser(ReactApiUserDto apiUserToCreate, String baseUrl) {
		ApiUser apiUser = new ApiUser();
		try {
			// Validate for duplicate UserName
			if (getUserByUserNameOrEmail(apiUserToCreate.getUserName()) != null) {
				apiUser.setErrorCode(2000);
				apiUser.setErrorMessage("Username Already Exists");
				return apiUser;
			}
			// Validate for duplicate Email Id.
			if (getUserByUserNameOrEmail(apiUserToCreate.getEmailId()) != null) {
				apiUser.setErrorCode(2000);
				apiUser.setErrorMessage("Email Already Exists");
				return apiUser;
			}
			logger.debug("Creating new Api User [{}]", apiUserToCreate.getUserName());

			apiUser.setLocked(apiUserToCreate.isLocked());
			apiUser.setUserName(apiUserToCreate.getUserName());
			apiUser.setPassword(PasswordUtil.generatePassword());

			apiUser.setFirstName(apiUserToCreate.getFirstName());
			apiUser.setLastName(apiUserToCreate.getLastName());
			apiUser.setEmail(apiUserToCreate.getEmailId());

			ApiUser createdUser = crudRepository.createEntity(ApiUser.class, apiUser);
			logger.debug("Created new Api User with ID : {}", createdUser.getId());

			apiUserToCreate.getApiMappingIds().split(",");

			return createdUser;
		} catch (Exception e) {
			logger.error("Exception while API User creation:{}", e.getMessage());
			apiUser.setErrorCode(2000);
			apiUser.setErrorMessage("Error in Creating Api User");
			return apiUser;
		}
	}

	@Override
	public ApiUser updateApiUser(long userId, ReactApiUserDto apiUser) {
		ApiUser original = getUserById(userId);
		try {
			original.setLocked(apiUser.isLocked());

			original.setFirstName(apiUser.getFirstName());
			original.setLastName(apiUser.getLastName());
			original.setEmail(apiUser.getEmailId());

			return crudRepository.mergeEntity(ApiUser.class, original);
		} catch (Exception e) {
			logger.error("Exception while updating api user:{}", e.getMessage());
			return null;
		}
	}

	@Override
	public ApiUser getUserByPassword(String password) {
		ApiUser user = null;
		try {
			user = crudRepository.getSingleEntity(CrudRepository.QueryFilter.newInstance(ApiUser.class)
					.addQueryAttribute("password", password, Repository.QueryMode.EQ, String.class));
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return user;
	}

	@Override
	public ApiUser forgotPasswrd(ApiUser userProfile, String baseUrl) {
		ApiUser user = null;
		try {
			user = getUserByUserName(userProfile.getUserName());
			if (user != null && user.getId() > 0) {
				logger.debug("Resetting Password for : {}", user.getUserName());
				// user.setPassword(PasswordUtil.generatePassword());
				// crudRepository.mergeEntity(ApiUser.class, user);

			} else {
				logger.debug("User not found");
				return user;
			}

		} catch (Exception exception) {
			logger.error("Error in resetting password : {}", exception.getMessage());
		}
		logger.debug("Reset password successful");
		return user;
	}

	@Override
	public void deleteApiUser(ApiUser user) {
		try {
			user.setDeleted(true);
			user.setDeleteDate(new Date());
			crudRepository.mergeEntity(ApiUser.class, user);

		} catch (Exception e) {
			logger.error("Unable to delete the user : {}", e.getMessage());
		}
	}

}
