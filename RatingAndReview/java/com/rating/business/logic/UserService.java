package com.rating.business.logic;

import java.util.List;

import com.rating.dto.ReactUserDto;
import com.rating.dto.UserDTO;
import com.rating.exc.RatingAndReviewException;
import com.rating.utils.order.OrderParams;
import com.rating.bo.User;
import com.rating.utils.search.PaginationSearchParams;
import com.rating.utils.search.PaginationSearchResult;

public interface UserService extends CrudService {

	public PaginationSearchResult findAllUsersForDataTable(PaginationSearchParams paginationSearchParams,
			OrderParams orderparams, User userProfile);

	public User getUserByUserName(String userName);

	public User getUserById(long id);

	public UserDTO getUserDTOById(long id);

	public List<User> findUsersByAccountId(long accountId);

	public User createUser(User userDataToCreate, String baseUrl);

	public User updateUser(UserDTO updatedUser);
	
	public User updateUserProfile(User updatedUser, Long countryId, Long timeZoneId, String telephoneNumber, 
			String mobileNumber, String emailAddress);

	public void deleteByUser(User user);

	public User suspendByUserName(String userName);

	public User activateByUserName(String userName);

	public User getUserByPassword(String password);

	public User setPassword(String newPassword);

	public User forgotUserPassword(User userProfile, String baseUrl);



	@Deprecated
	public List<User> findNewUsers();

	public void updateUserList(List<User> userList);

	public User getUserByUserNameOrEmail(String userNameOrEmailId) throws RatingAndReviewException;

	public UserDTO getUserDTOByUserNameOrEmail(String userNameOrEmailId) throws RatingAndReviewException;

	Long getRoleReferenceCount(Long roleId);

	

	public void updateUser(User user);
	
	public User updateUserById(long userId, ReactUserDto user);
	
	public User addUser(ReactUserDto userDataToCreate, String baseUrl);
	
	public List<User> getActiveUsers();

	User getUserByEmail(String userNameOrEmailId) throws RatingAndReviewException;

	public List<Long> getUserIdsByGroupIdsOrAccountIds(List<Long> accountsIds, List<Long> groupIds);
}
