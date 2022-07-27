package com.rating.business.logic;

import java.util.Map;

import com.rating.dto.ApiResponse;
import com.rating.dto.ReactApiUserDto;
import com.rating.utils.order.OrderParams;
import com.rating.bo.ApiUser;
import com.rating.bo.User;
import com.rating.utils.search.PaginationSearchParams;
import com.rating.utils.search.PaginationSearchResult;

/**
 * @author Ankita Shrothi
 *
 */
public interface ApiUserService extends CrudService {

	/**
	 * @param params
	 * @return
	 */
	ApiResponse getUsers(Map<String, Object> params);

	/**
	 * @param params
	 * @return
	 */
	ApiResponse getUser(Map<String, Object> params);

	/**
	 * @param token
	 * @return
	 */
	ApiUser getUserByToken(String token);

	/**
	 * @param userName
	 * @return
	 */
	ApiUser getUserByUserName(String userName);

	/**
	 * @param params
	 * @return
	 */
	ApiResponse createOrUpdateUser(Map<String, Object> params);

	/**
	 * @param params
	 * @return
	 */
	ApiResponse deleteUser(Map<String, Object> params);

	/**
	 * @param params
	 * @return
	 */
	ApiResponse suspendUser(Map<String, Object> params);

	/**
	 * @param params
	 * @return
	 */
	ApiResponse resumeUser(Map<String, Object> params);

	/**
	 * @param params
	 * @return
	 */
	ApiResponse forgotPassword(Map<String, Object> params);

	/**
	 * @param params
	 * @return
	 */
	ApiResponse changePassword(Map<String, Object> params);

	/**
	 * @param params
	 * @return
	 */
	ApiResponse generatePassword(Map<String, Object> params);

	/**
	 * @param ApiUsers
	 * @return
	 */
	public PaginationSearchResult findAllApiUserForDataTable(PaginationSearchParams paginationSearchParams,
			OrderParams orderparams, User userProfile);

	/**
	 * @param ApiUsers by ID
	 * @return
	 */
	public ApiUser getUserById(long id);	

	
	
	
	public ApiUser addApiUser(ReactApiUserDto apiUserToAdd, String baseUrl);
	
	public ApiUser updateApiUser(long userId, ReactApiUserDto apiUser);
	
	public ApiUser getUserByPassword(String password);
	
	public ApiUser forgotPasswrd(ApiUser userProfile, String baseUrl);
	
	public void deleteApiUser(ApiUser user);
}
