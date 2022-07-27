package com.rating.business.logic.impl;

/**
 * 
 * Version | Bug Id | Author Name | Modified Date |Description
 *
 *  1.1     |  326   | Mritunjay   |02-March-2016  |BUG_ID#326 : Forgot password is not working.
  * 1.2     |  CR#76   | Mritunjay   |10-March-2016  |CR#76 : TO provide option to login as user name and email id. 
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.rating.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rating.dl.CrudRepository;
import com.rating.dl.QueryFilterTemplate;
import com.rating.dl.Repository;
import com.rating.dl.Repository.QueryMode;
import com.rating.dl.Repository.SortAttribute.SortMode;
import com.rating.exc.RatingAndReviewException;
import com.rating.transform.TransformProcessor;
import com.rating.utils.PasswordUtil;
import com.rating.utils.order.OrderParams;
import com.rating.bo.User;
import com.rating.business.logic.CommonService;
import com.rating.business.logic.UserService;
import com.rating.utils.search.PaginationSearchParams;
import com.rating.utils.search.PaginationSearchResult;

@Service
@Transactional("RatingAndReviewTransactionManager")
public class UserServiceImpl extends CrudServiceImpl implements UserService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CrudRepository crudRepository;

	

	@Autowired
	private TransformProcessor transformProcessor;



	

	@Autowired
	private ResourceBundleMessageSource messageSource;
	@Autowired
	private CommonService commonService;

	@Resource
	private Environment env;

	public UserServiceImpl() {
		// random = new SecureRandom();
	}

	public User getUserByUserName(String userName) {
		try {
			User user = crudRepository.getSingleEntity(CrudRepository.QueryFilter.newInstance(User.class)
					.addQueryAttribute("userName", userName, Repository.QueryMode.EQ, String.class));
			if (user != null) {
				// return transformProcessor.transformTo(user, User.class);

				return user;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public User getUserById(long id) {
		User userProfile = getEntityById(User.class, id);
		return userProfile;// transformProcessor.transformTo(userProfile, User.class);
	}

	public UserDTO getUserDTOById(long id) {
		User userProfile = getEntityById(User.class, id);

		if (userProfile == null) {
			return null;
		}
		UserDTO userDTO = transformProcessor.transformTo(userProfile, UserDTO.class);
	
		return userDTO;
	}

	public List<User> findUsersByAccountId(long accountId) {
		return crudRepository.getEntityEntries(CrudRepository.QueryFilter.newInstance(User.class)
				.addQueryAttribute("account.id", accountId, Repository.QueryMode.EQ, Long.class));
	}

	/**
	 * This method is used to Create User
	 *
	 * @see com.rating.business.logic.UserService#createUser(com.rating.bo.
	 *      User)
	 */
	@Override
	public User createUser(User userDataToCreate, String baseUrl) {

		/*
		 * Validate for duplicate User
		 */
		try {
			if (getUserByUserNameOrEmail(userDataToCreate.getUserName()) != null) {
				userDataToCreate.setErrorCode(2000);
				userDataToCreate.setErrorMessage("Username Already Exists");
				return userDataToCreate;
			}

			logger.debug("Creating new User [ " + userDataToCreate.getUserName() + " ]");

			User user = new User();

			
			user.setLocked(userDataToCreate.isLocked());
			user.setUserName(userDataToCreate.getUserName());
			// byte passwd[] = random.generateSeed(50);
			user.setPassword(PasswordUtil.generatePassword());
// user.setUserAction(1);

			logger.debug("Creating new user in DB");
			User createdUser = crudRepository.createEntity(User.class, user);

			logger.debug("Created new User with ID :" + createdUser.getId());

			
			return createdUser;

		} catch (RatingAndReviewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			userDataToCreate.setErrorCode(2000);
			userDataToCreate.setErrorMessage("Error in Creating User");
		} catch (Exception e) {
			e.printStackTrace();
			userDataToCreate.setErrorCode(2000);
			userDataToCreate.setErrorMessage("Error in Creating User");

		}
		return userDataToCreate;
	}

	/**
	 * This method is used to update User
	 */
	public User updateUser(UserDTO updatedUser) {
		User original = getUserById(updatedUser.getId());

		original.setLocked(updatedUser.isLocked());

		// if(!updatedUser.getContactInfo().getEmail().equalsIgnoreCase(original.getContactInfo().getEmail())){
		// logger.debug("Email address updated to
		// "+updatedUser.getContactInfo().getEmail());
		// try {
		// if (getUserByUserNameOrEmail(updatedUser.getContactInfo().getEmail()) !=
		// null) {
		// logger.warn("User exist with updated Email address");
		// original.setErrorCode(-1);
		// original.setErrorMessage("User already exists with Email address");
		// return original;
		// }
		// } catch (RatingAndRevieException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// } else {
		// logger.debug("Email address not changed");
		// }

	
		if ((updatedUser.getFlag() != null && updatedUser.getFlag().equalsIgnoreCase("passwordChange"))) {
			if (!PasswordUtil.matchPassword(updatedUser.getCurrentPassword(), original.getPassword())) {
				original.setErrorCode(-1);
				original.setErrorMessage("Current password mismatch");
				return original;
			}

			String key = updatedUser.getPassword();

			if (StringUtils.isBlank(key)) {
				original.setErrorCode(-1);
				original.setErrorMessage("Please provide password");
				return original;
			}

			if (key.length() < 8 && key.length() > 16) {
				original.setErrorCode(-1);
				original.setErrorMessage("Password length must be between 8 to 16 characters");
				return original;
			}

			if (!(key.matches("(.*[A-Z].*)") && key.matches("(.*[a-z].*)") && key.matches("(.*[0-9].*)")
					&& key.matches("(.*[!,@,#,$,%,^,&,*].*$)"))) {
				original.setErrorCode(-1);
				original.setErrorMessage("Please provide password according to password policy");
				return original;
			}

			if (!updatedUser.getPassword().equals(updatedUser.getConfirmPassword())) {
				original.setErrorCode(-1);
				original.setErrorMessage("New passwords doesn't match");
				return original;
			}
			original.setPassword(PasswordUtil.encryptPassword(updatedUser.getPassword()));
		}

		return crudRepository.mergeEntity(User.class, original);
	}

	/**
	 * This method is used to Update User Profile
	 */
	public User updateUserProfile(User user, Long countryId, Long timeZoneId, String telephoneNumber,
			String mobileNumber, String emailAddress) {

		try {
			user.setLocked(user.isLocked());



			return crudRepository.mergeEntity(User.class, user);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public User addUser(ReactUserDto userDataToCreate, String baseUrl) {
		User user = new User();
		try {
			// Validate for duplicate User
			if (getUserByUserNameOrEmail(userDataToCreate.getUserName()) != null) {
				user.setErrorCode(2000);
				user.setErrorMessage("Username Already Exists");
				return user;
			}
			logger.debug("Creating new User [{}]", userDataToCreate.getUserName());

			
			user.setLocked(userDataToCreate.isLocked());
			user.setUserName(userDataToCreate.getUserName());

			user.setPassword(PasswordUtil.generatePassword());

			

			logger.debug("Creating new user in DB");
			User createdUser = crudRepository.createEntity(User.class, user);

			logger.debug("Created new User with ID : {}", createdUser.getId());

			return createdUser;
		} catch (Exception e) {
			logger.error("Exception while user creation:{}", e.getMessage());
			user.setErrorCode(2000);
			user.setErrorMessage("Error in Creating User");
		}
		return user;
	}

	/**
	 * This method is used to Update User by User Id
	 */
	@Override
	public User updateUserById(long userId, ReactUserDto user) {
		User original = getUserById(userId);
		try {
			original.setLocked(user.isLocked());
			if (!user.isLocked()) {
				original.setAttempts(0);
			}
			
			return crudRepository.mergeEntity(User.class, original);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This method is used to delete User by setting the setDelete as True modified
	 * By Ankita
	 * 
	 * implemented https://github.com/peeyush-tm/RatingAndReview-master/issues/3222
	 * 
	 * Implement Delete Profile functionality #3222
	 */
	public void deleteByUser(User user) {
		try {
			user.setDeleted(true);
			user.setDeleteDate(new Date());
			
			
			

			String query = "UPDATE users" + " SET USER_NAME= \'"
					+ messageSource.getMessage("delete.user.name", null, Locale.ENGLISH) + "\' WHERE USER_NAME= \'"
					+ user.getUserName() + "\'";
			crudRepository.updateSqlQuery(query, null);
			query = "UPDATE auditlog" + " SET USERNAME= \'"
					+ messageSource.getMessage("delete.user.name", null, Locale.ENGLISH) + "\' WHERE USERNAME = \'"
					+ user.getUserName() + "\'";
			crudRepository.updateSqlQuery(query, null);
			query = "UPDATE auditlog_end_points" + " SET USERNAME = \'"
					+ messageSource.getMessage("delete.user.name", null, Locale.ENGLISH) + "\' WHERE USERNAME = \'"
					+ user.getUserName() + "\'";
			crudRepository.updateSqlQuery(query, null);
			query = "UPDATE iotsmp_cdr_data.rule_engine_auditlog" + " SET USERNAME = \'"
					+ messageSource.getMessage("delete.user.name", null, Locale.ENGLISH) + "\' WHERE USERNAME = \'"
					+ user.getUserName() + "\'";

			crudRepository.updateSqlQuery(query, null);
			user.setUserName(messageSource.getMessage("delete.user.name", null, Locale.ENGLISH));

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occured while updataing SIM Pool.");
			throw e;
		}

		crudRepository.mergeEntity(User.class, user);

	}

	/**
	 * This method is used to Suspend User by User Name
	 */
	public User suspendByUserName(String userName) {
		User user = getUserByUserName(userName);
		user.setLocked(true);
		return createEntity(User.class, user);
	}

	/**
	 * This Method is used to Activate the user by User name
	 */
	public User activateByUserName(String userName) {
		User user = getUserByUserName(userName);
		user.setLocked(false);
		return createEntity(User.class, user);
	}

	public User getUserByPassword(String password) {
		User user = null;
		try {
			user = crudRepository.getSingleEntity(CrudRepository.QueryFilter.newInstance(User.class)
					.addQueryAttribute("password", password, Repository.QueryMode.EQ, String.class));
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return user;
	}

	public User setPassword(String newPassword) {
		User user = crudRepository.getSingleEntity(CrudRepository.QueryFilter.newInstance(User.class)
				.addQueryAttribute("userName", newPassword, Repository.QueryMode.EQ, String.class));
		crudRepository.createEntity(User.class, user);
		return user;
	}

	@Override
	public PaginationSearchResult findAllUsersForDataTable(PaginationSearchParams paginationSearchParams,
			final OrderParams orderParams, final User userProfile) {

		return new QueryFilterTemplate<PaginationSearchParams>(this.crudRepository, paginationSearchParams,
				transformProcessor) {

			public CrudRepository.QueryFilter buildSearchFilter(PaginationSearchParams searchContainer) {

				Repository.ComplexQueryAttribute complexQueryAttribute = Repository.ComplexQueryAttribute
						.newInstance(Repository.ComplexQueryMode.OR);

			

				CrudRepository.QueryFilter<User> queryFilter = CrudRepository.QueryFilter.newInstance(User.class);

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
						Repository.ComplexQueryAttribute searchQueryAttribute = commonService.searchTableData("user",
								searchContainer.getSearchValue());
						queryFilter.addComplexQueryAttribute(searchQueryAttribute);
					} catch (RatingAndReviewException e) {
						e.printStackTrace();
					}
				}
				return queryFilter;
			}
		}.load(User.class);
	}

	/**
	 * This method is used when user forgot Password
	 */
	@Override
	public User forgotUserPassword(User userProfile, String baseUrl) {
		User user = null;
		try {
			user = getUserByUserName(userProfile.getUserName());
			if (user != null && user.getId() > 0) {
				logger.debug("Resetting Password for: {}", user.getUserName());
				// To identify forgot password
				user.setUserAction(2);
				crudRepository.mergeEntity(User.class, user);

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
	public List<User> findNewUsers() {
		return crudRepository.getEntityEntries(CrudRepository.QueryFilter.newInstance(User.class)
				.addQueryAttribute("isNewUser", true, Repository.QueryMode.EQ, Boolean.class));
	}

	@Override
	public void updateUserList(List<User> userList) {
		crudRepository.createEntityList(User.class, userList);

	}

	/**
	 * This method is used to get User by User Name Or Email id
	 *
	 * @version 1.2
	 * @param userNameOrEmailId
	 * @return
	 * @throws RatingAndReviewException
	 */
	@Override
	public User getUserByUserNameOrEmail(String userNameOrEmailId) throws RatingAndReviewException {
		User user = null;
		try {
			Repository.ComplexQueryAttribute complexQueryAttribute = Repository.ComplexQueryAttribute
					.newInstance(Repository.ComplexQueryMode.OR);
			complexQueryAttribute.addQueryAttribute(Repository.QueryAttribute.newInstance("userName", userNameOrEmailId,
					Repository.QueryMode.EQ_IGNORE_CASE, String.class));
			// complexQueryAttribute.addQueryAttribute(Repository.QueryAttribute.newInstance("contact.email",
			// userNameOrEmailId, Repository.QueryMode.EQ_IGNORE_CASE, String.class,
			// "contactInfo", "contact"));
			user = crudRepository.getSingleEntity(
					CrudRepository.QueryFilter.newInstance(User.class).addComplexQueryAttribute(complexQueryAttribute));

		} catch (Exception exception) {
			throw new RatingAndReviewException("Unable to find user with Username: " + userNameOrEmailId, exception);
		}
		// if(user != null){
		// user = transformProcessor.transformTo(user, User.class);
		// }

		return user;
	}

	/**
	 * This method is used to get User by User Name Or Email id
	 *
	 * @version 1.2
	 * @param userNameOrEmailId
	 * @return
	 * @throws RatingAndReviewException
	 */
	@Override
	public User getUserByEmail(String userNameOrEmailId) throws RatingAndReviewException {
		List<User> user = null;
		try {
			Repository.ComplexQueryAttribute complexQueryAttribute = Repository.ComplexQueryAttribute
					.newInstance(Repository.ComplexQueryMode.OR);
			complexQueryAttribute.addQueryAttribute(Repository.QueryAttribute.newInstance("userName", userNameOrEmailId,
					Repository.QueryMode.EQ_IGNORE_CASE, String.class));
			complexQueryAttribute.addQueryAttribute(Repository.QueryAttribute.newInstance("contact.email",
					userNameOrEmailId, Repository.QueryMode.EQ_IGNORE_CASE, String.class, "contactInfo", "contact"));
			// user = crudRepository.getSingleEntity(
			// CrudRepository.QueryFilter.newInstance(User.class).addComplexQueryAttribute(complexQueryAttribute));

			user = crudRepository.getEntityEntries(
					CrudRepository.QueryFilter.newInstance(User.class).addComplexQueryAttribute(complexQueryAttribute));

		} catch (Exception exception) {
			exception.printStackTrace();
			throw new RatingAndReviewException("Unable to find user with Username: " + userNameOrEmailId, exception);
		}
		if (user == null || user.size() <= 0) {
			// user = transformProcessor.transformTo(user, User.class);
			return null;
		}
		return user.get(0);
	}

	@Override
	public UserDTO getUserDTOByUserNameOrEmail(String userNameOrEmailId) throws RatingAndReviewException {
		User user = null;
		try {
			Repository.ComplexQueryAttribute complexQueryAttribute = Repository.ComplexQueryAttribute
					.newInstance(Repository.ComplexQueryMode.OR);
			complexQueryAttribute.addQueryAttribute(Repository.QueryAttribute.newInstance("userName", userNameOrEmailId,
					Repository.QueryMode.EQ, String.class));
			// complexQueryAttribute.addQueryAttribute(Repository.QueryAttribute.newInstance("contact.email",
			// userNameOrEmailId, Repository.QueryMode.EQ, String.class, "contactInfo",
			// "contact"));
			user = crudRepository.getSingleEntity(
					CrudRepository.QueryFilter.newInstance(User.class).addComplexQueryAttribute(complexQueryAttribute));
		} catch (Exception exception) {
			throw new RatingAndReviewException("Unable to find user with Username: " + userNameOrEmailId, exception);
		}

	
		
		return transformProcessor.transformTo(user, UserDTO.class);
	}

	@Override
	public Long getRoleReferenceCount(Long roleId) {
		return crudRepository.countEntityEntries(CrudRepository.QueryFilter.newInstance(User.class)
				.addQueryAttribute("role.id", roleId, Repository.QueryMode.EQ, Long.class));

	}


	@Override
	public void updateUser(User user) {
		crudRepository.mergeEntity(User.class, user);
	}

	public List<User> getActiveUsers() {
		CrudRepository.QueryFilter<User> queryFilter = CrudRepository.QueryFilter.newInstance(User.class);
		queryFilter.addQueryAttribute("locked", false, Repository.QueryMode.EQ, Boolean.class);
		queryFilter.addQueryAttribute("deleted", false, Repository.QueryMode.EQ, Boolean.class);
		return crudRepository.getEntityEntries(queryFilter);
	}

	@Override
	public List<Long> getUserIdsByGroupIdsOrAccountIds(List<Long> accountsIds, List<Long> groupIds) {
		List<Long> userIds = new ArrayList<>();
		try {
			CrudRepository.QueryFilter<User> queryFilter = CrudRepository.QueryFilter.newInstance(User.class);
			Repository.ComplexQueryAttribute complexQueryAttribute = Repository.ComplexQueryAttribute
					.newInstance(Repository.ComplexQueryMode.OR);
			complexQueryAttribute.addQueryAttribute(
					Repository.QueryAttribute.newInstance("account.id", accountsIds, QueryMode.IN, null));

			if (groupIds != null) {
				complexQueryAttribute.addQueryAttribute(
						Repository.QueryAttribute.newInstance("group.id", groupIds, QueryMode.IN, null));

			}

			queryFilter.addComplexQueryAttribute(complexQueryAttribute);

			List<User> userList = crudRepository.getEntityEntries(queryFilter);
			userIds = userList.stream().map(User::getId).distinct().collect(Collectors.toList());

		} catch (Exception e) {
			logger.error("Error in getGroupIdsByAccountIds: {}", e.getMessage());
		}
		return userIds;
	}


}
