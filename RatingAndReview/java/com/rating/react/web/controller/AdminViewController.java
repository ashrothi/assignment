/**
 * This package contain the controller classes for RatingAndReview React-UI Application.
 */
package com.rating.react.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rating.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rating.exc.RatingAndReviewException;
import com.rating.transform.TransformProcessor;
import com.rating.utils.datatable.DataTableResponse;
import com.rating.utils.datatable.DataTablesInput;
import com.rating.utils.datatable.SearchParameter;
import com.rating.utils.order.OrderParams;
import com.rating.bo.*;
import com.rating.business.logic.ApiUserService;
import com.rating.business.logic.CommonService;
import com.rating.business.logic.UserService;
import com.rating.utils.search.PaginationSearchParams;
import com.rating.utils.search.PaginationSearchResult;

/**
 * This controller class is used for Getting and Manipulating required Data on
 * the Admin page.
 * 
 * @author Ankita Shrothi
 */
@Controller
@Transactional(transactionManager = "RatingAndReviewTransactionManager")
public class AdminViewController {
	private Logger logger = LoggerFactory.getLogger(getClass());

	// Initialize the required objects.
	@Autowired
	private UserService userService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private TransformProcessor transformProcessor;

	@Autowired
	private ObjectMapper objectMapper;

	

	@Autowired
	private ApiUserService apiUserService;

	@Resource
	private Environment env;

	private static final String ACCOUNT_ID = "accountId";

	private static final String ERROR_MESSAGE = "errorMessage";

	private static final String SYS_CONFIG = "System Configuration [";

	private static final String ERROR_STATUS = "Error";
	private static final String SUCCESS = "Success";
	private static final String STATUS = "status";
	private static final String MESSAGE = "message";

	/********************************************
	 * Users *
	 ********************************************/
	/**
	 * This API is used for getting the Datatable grid for the User page.
	 * 
	 * @param offset      : Here pass the initial record number for server side
	 *                    Pagination.
	 * @param limit       : Here pass the last record number for server side
	 *                    Pagination.
	 * @param searchValue : Here pass the custom input value in datatable for server
	 *                    side Search.
	 * @param sortColumn  : Here pass the custom column name in datatable for server
	 *                    side Sorting.
	 * @param order       : Here pass the order to be used on column for server side
	 *                    Sorting.
	 * @param request     : To get http request header.
	 * @param response    : To return http response header.
	 * @return It will return grid for the User page in Datatable format.
	 */
	@RequestMapping(value = "/api/admin/users")
	public ResponseEntity<DataTableResponse> getAdminViewUsers(@RequestParam(required = true) Integer offset,
			@RequestParam(required = true) Integer limit, @RequestParam(required = false) String searchValue,
			@RequestParam(required = false) String sortColumn, @RequestParam(required = false) String order,
			HttpServletRequest request, HttpServletResponse response) {

		DataTablesInput dataTablesInput = new DataTablesInput();
		dataTablesInput.setDraw(1);
		dataTablesInput.setStart(offset);
		dataTablesInput.setLength(limit);
		if (null != searchValue) {
			dataTablesInput.setSearch(new SearchParameter(searchValue, null));
		}
		dataTablesInput.setOrder(new ArrayList<>());

		PaginationSearchResult paginationSearchResult = null;
		DataTableResponse dataTableResponse = null;
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			User userProfile = userService.getUserByUserNameOrEmail(auth.getName());

			if (userProfile != null) {
				if ((null != sortColumn && !sortColumn.isEmpty()) && (null != order && !order.isEmpty())) {
					paginationSearchResult = userService.findAllUsersForDataTable(
							PaginationSearchParams.buildSearchParams(dataTablesInput),
							OrderParams.getOrderParam(sortColumn, order), userProfile);
				} else {
					paginationSearchResult = userService.findAllUsersForDataTable(
							PaginationSearchParams.buildSearchParams(dataTablesInput),
							OrderParams.buildOrderParams(dataTablesInput), userProfile);
				}
				// convert to dto
				Function<User, UserDTO> toUserDTO = (entity) -> {

					UserDTO userDTO = transformProcessor.transformTo(entity, UserDTO.class);

					return userDTO;
				};

				List<UserDTO> resultDTOs = new ArrayList<>();
				paginationSearchResult.getResults().forEach(c -> resultDTOs.add(toUserDTO.apply((User) c)));

				dataTableResponse = new DataTableResponse(resultDTOs, paginationSearchResult.getRowCount(),
						paginationSearchResult.getRowCount(), Long.valueOf(dataTablesInput.getDraw()));
			} else {
				dataTableResponse = new DataTableResponse(new ArrayList<User>(), 1l, 0l, 0l);
			}
		} catch (Exception exception) {
			dataTableResponse = new DataTableResponse(new ArrayList<User>(), 1l, 0l, 0l);
			logger.error("Exception occurred while retreiving Users data table: {}", exception.getMessage());
		}
		return new ResponseEntity<>(dataTableResponse, HttpStatus.OK);
	}

	/********************************************
	 * User Information By Id *
	 ********************************************/
	/**
	 * 
	 * @param userId :pass the user id to which information is needed
	 * @return
	 */
	@GetMapping(value = "/api/get/user/{userId}")
	public ResponseEntity<UserDTO> getUser(@PathVariable long userId) {
		try {
			UserDTO userDTO = userService.getUserDTOById(userId);
			if (userDTO == null) {
				userDTO = new UserDTO();
				userDTO.setErrorCode(400);
				userDTO.setErrorMessage("User not found.");
				return new ResponseEntity<>(userDTO, HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>(userDTO, HttpStatus.OK);
		} catch (Exception exception) {
			logger.error("Exception occurred while getting user by id: {}", exception.getMessage());
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	/********************************************
	 * Add User Information *
	 ********************************************/
	/**
	 * 
	 * @param parameters: Here pass the required parameter to create the user
	 * @param request     : To get http request header.
	 * @param response    : To return http response header.
	 * @return
	 * @throws RatingAndReviewException
	 */
	@PostMapping(value = "/api/add/user")
	public ResponseEntity<?> addNewUser(@RequestParam Map<String, Object> parameters, HttpServletRequest request,
			HttpServletResponse response) throws RatingAndReviewException {
		String baseUrl = request.getHeader("origin") + "/CreatePassword/";
		User createdUser = new User();
		String strRequest = null;
		ReactUserDto user = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		System.out.println("parameters " + parameters);
		User userProfile = userService.getUserByUserNameOrEmail(auth.getName());
		try {
			strRequest = objectMapper.writeValueAsString(parameters);
			user = objectMapper.readValue(strRequest, ReactUserDto.class);
		} catch (IOException ioException) {
			logger.error("Exception while parsing request: {}", ioException.getMessage());
			createdUser.setErrorMessage("Exception while parsing request: " + ioException.getMessage());
			return new ResponseEntity<>(createdUser, HttpStatus.OK);
		}
		createdUser = userService.addUser(user, baseUrl);
		if (createdUser.getErrorCode() != 0) {
			if (createdUser.getErrorCode() == 2000) {

				return new ResponseEntity<>(transformProcessor.transformTo(createdUser, UserDTO.class),
						HttpStatus.CONFLICT);
			}

			return new ResponseEntity<>(createdUser, HttpStatus.INTERNAL_SERVER_ERROR);
		} else {

			UserDTO createdUserDTO = transformProcessor.transformTo(createdUser, UserDTO.class);
			return new ResponseEntity<>(createdUserDTO, HttpStatus.OK);
		}
	}

	/********************************************
	 * Delete User *
	 ********************************************/
	/**
	 * 
	 * @param userId   : Here pass the id of the user need to delete
	 * @param request  : To get http request header.
	 * @param response : To return http response header.
	 * @return
	 * @throws RatingAndReviewException
	 */
	@DeleteMapping(value = "/api/delete/user/{userId}")
	public ResponseEntity<Map<String, String>> deleteUserById(@PathVariable("userId") long userId,
			HttpServletRequest request, HttpServletResponse response) throws RatingAndReviewException {
		Map<String, String> responseMap = new HashMap<>();
		User userToDelete = new User();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User loginUser = userService.getUserByUserNameOrEmail(auth.getName());
		userToDelete = this.userService.getUserById(userId);
		String userName = userToDelete.getUserName();
		try {

			userService.deleteByUser(userToDelete);

			responseMap.put(STATUS, SUCCESS);
			responseMap.put(MESSAGE, "User Deleted Successfully.");
			// a

			return new ResponseEntity<>(responseMap, HttpStatus.OK);
		} catch (Exception e) {
			responseMap.put(STATUS, ERROR_STATUS);
			responseMap.put(MESSAGE, "Exception Occured :" + e.getMessage());
			//

			return new ResponseEntity<>(responseMap, HttpStatus.OK);
		}
	}

	/********************************************
	 * API Users *
	 ********************************************/
	/**
	 * This API is used for getting the Datatable grid for the API User page.
	 * 
	 * @param offset      : Here pass the initial record number for server side
	 *                    Pagination.
	 * @param limit       : Here pass the last record number for server side
	 *                    Pagination.
	 * @param searchValue : Here pass the custom input value in datatable for server
	 *                    side Search.
	 * @param column      : Here pass the custom column name in datatable for server
	 *                    side Sorting.
	 * @param dir         : Here pass the order to be used on column for server side
	 *                    Sorting.
	 * @param request     : To get http request header.
	 * @param response    : To return http response header.
	 * @return It will return grid for the API User page in Datatable format.
	 */
	@GetMapping(value = "/api/get/apiUsers")
	public ResponseEntity<DataTableResponse> getAdminViewApiUsers(
			@RequestParam(value = "offset", required = true) Integer offset,
			@RequestParam(value = "limit", required = true) Integer limit,
			@RequestParam(value = "searchValue", required = false) String searchValue,
			@RequestParam(value = "sortColumn", required = false) String column,
			@RequestParam(value = "order", required = false) String dir, HttpServletRequest request,
			HttpServletResponse response) {

		DataTablesInput dataTablesInput = new DataTablesInput();
		dataTablesInput.setDraw(1);
		dataTablesInput.setStart(offset);
		dataTablesInput.setLength(limit);
		if (null != searchValue) {
			dataTablesInput.setSearch(new SearchParameter(searchValue, null));
		}
		dataTablesInput.setOrder(new ArrayList<>());

		PaginationSearchResult paginationSearchResult = null;
		DataTableResponse dataTableResponse = null;
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			User userProfile = userService.getUserByUserNameOrEmail(auth.getName());
			// logger.debug("account: {}", userProfile.getAccount().getId());

			// logger.debug("account: {}", userProfile.getAccount().getId());
			if (userProfile != null) {
				if ((null != column && !column.isEmpty()) && (null != dir && !dir.isEmpty())) {
					paginationSearchResult = apiUserService.findAllApiUserForDataTable(
							PaginationSearchParams.buildSearchParams(dataTablesInput),
							OrderParams.getOrderParam(column, dir), userProfile);
				} else {
					paginationSearchResult = apiUserService.findAllApiUserForDataTable(
							PaginationSearchParams.buildSearchParams(dataTablesInput),
							OrderParams.buildOrderParams(dataTablesInput), userProfile);
				}
				// convert to dto
				Function<ApiUser, ApiUserDTO> toApiUserDTO = (entity) -> {

					ApiUserDTO apiUserDTO = transformProcessor.transformTo(entity, ApiUserDTO.class);

					return apiUserDTO;
				};
				List<ApiUserDTO> resultDTOs = new ArrayList<>();
				paginationSearchResult.getResults().forEach(c -> resultDTOs.add(toApiUserDTO.apply((ApiUser) c)));

				dataTableResponse = new DataTableResponse(resultDTOs, paginationSearchResult.getRowCount(),
						paginationSearchResult.getRowCount(), Long.valueOf(dataTablesInput.getDraw()));
			} else {
				dataTableResponse = new DataTableResponse(new ArrayList<ApiUser>(), 1l, 0l, 0l);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			dataTableResponse = new DataTableResponse(new ArrayList<ApiUser>(), 1l, 0l, 0l);
			logger.error("Exception occurred while retreiving APIUser table: {}", exception.getMessage());
		}
		return new ResponseEntity<>(dataTableResponse, HttpStatus.OK);
	}

	/**
	 * This API is used for getting the API User details on the basis of id.
	 * 
	 * @param id : Here pass the id for which you need API User details.
	 * @return It will return API User details which id is passed.
	 */
	@GetMapping(value = "/api/get/apiUser/{id}")
	public ResponseEntity<ApiUserDTO> getApiUser(@PathVariable long id) {
		try {
			// convert to dto
			Function<ApiUser, ApiUserDTO> toApiUserDTO = (entity) -> {

				return transformProcessor.transformTo(entity, ApiUserDTO.class);
			};

			ApiUserDTO apiUserDTO = toApiUserDTO.apply(apiUserService.getUserById(id));

			return new ResponseEntity<>(apiUserDTO, HttpStatus.OK);
		} catch (Exception exception) {
			logger.error("Exception occurred while getting users: {}", exception.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * This API is used for Creating API Users.
	 * 
	 * @throws RatingAndReviewException
	 * 
	 */
	@PostMapping(value = "/api/create/apiUser")
	public ResponseEntity<?> addUser(@RequestParam Map<String, Object> parameters, HttpServletRequest request,
			HttpServletResponse response) throws RatingAndReviewException {
		String baseUrl = request.getHeader("origin") + "/SetMyPassword/";
		ApiUser newApiUser = new ApiUser();
		String strRequest = null;
		ReactApiUserDto apiUser = null;
		try {
			strRequest = objectMapper.writeValueAsString(parameters);
			apiUser = objectMapper.readValue(strRequest, ReactApiUserDto.class);
		} catch (IOException ioException) {
			logger.error("Exception while parsing request: {}", ioException.getMessage());
			newApiUser.setErrorCode(400);
			newApiUser.setErrorMessage("Exception while parsing request: " + ioException.getMessage());
			return new ResponseEntity<>(new ApiUserDTO(), HttpStatus.BAD_REQUEST);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.getUserByUserNameOrEmail(auth.getName());
		// convert ApiUser entity to ApiUserDTO object
		Function<ApiUser, ApiUserDTO> toUserDTO = (entity) -> transformProcessor.transformTo(entity, ApiUserDTO.class);

		if (commonService.isAlreadyExist("userName", apiUser.getUserName(), null, ApiUser.class, null)) {
			newApiUser.setErrorCode(409);
			newApiUser.setErrorMessage("Api Username Already Exist");
			ApiUserDTO newApiUserDTO = toUserDTO.apply(newApiUser);
			return new ResponseEntity<>(newApiUserDTO, HttpStatus.CONFLICT);
			// return new ResponseEntity<>(newApiUser, HttpStatus.CONFLICT);
		}
		if (commonService.isAlreadyExist("email", apiUser.getEmailId(), null, ApiUser.class, null)) {
			newApiUser.setErrorCode(409);
			newApiUser.setErrorMessage("Email Already Exist");
			ApiUserDTO newApiUserDTO = toUserDTO.apply(newApiUser);
			return new ResponseEntity<>(newApiUserDTO, HttpStatus.CONFLICT);
			// return new ResponseEntity<>(newApiUser, HttpStatus.CONFLICT);
		}
		newApiUser = apiUserService.addApiUser(apiUser, baseUrl);

		if (newApiUser.getErrorCode() != 0) {

			ApiUserDTO newApiUserDTO = toUserDTO.apply(newApiUser);
			return new ResponseEntity<>(newApiUserDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		} else {

			ApiUserDTO newApiUserDTO = toUserDTO.apply(newApiUser);
			return new ResponseEntity<>(newApiUserDTO, HttpStatus.OK);
		}
	}

}
