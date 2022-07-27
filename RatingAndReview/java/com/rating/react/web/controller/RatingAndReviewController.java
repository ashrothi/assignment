/**
 * This package contain the controller classes for RatingAndReview React-UI Application.
 */
package com.rating.react.web.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import com.rating.business.logic.CommonService;
import com.rating.business.logic.RatingAndReviewService;
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
public class RatingAndReviewController {
	private Logger logger = LoggerFactory.getLogger(getClass());

	// Initialize the required objects.
	@Autowired
	private RatingAndReviewService ratingAndReviewService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private TransformProcessor transformProcessor;

	@Autowired
	private ObjectMapper objectMapper;

	@Resource
	private Environment env;

	private static final String ERROR_STATUS = "Error";
	private static final String SUCCESS = "Success";
	private static final String STATUS = "status";
	private static final String MESSAGE = "message";

	/********************************************
	 * Add RatingAndReview Information *
	 ********************************************/
	/**
	 * 
	 * @param parameters: Here pass the required parameter to create the
	 *                    RatingAndReview
	 * @param request     : To get http request header.
	 * @param response    : To return http response header.
	 * @return
	 * @throws RatingAndReviewException
	 */
	@PostMapping(value = "/api/add/review")
	public ResponseEntity<?> addNewUser(@RequestParam Map<String, Object> parameters, HttpServletRequest request,
			HttpServletResponse response) throws RatingAndReviewException {
		String baseUrl = request.getHeader("origin") + "/CreatePassword/";
		RatingAndReview createdRating = new RatingAndReview();
		String strRequest = null;
		RatingAndReview rating = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		System.out.println("parameters " + parameters);

		try {
			strRequest = objectMapper.writeValueAsString(parameters);
			rating = objectMapper.readValue(strRequest, RatingAndReview.class);
		} catch (IOException ioException) {
			logger.error("Exception while parsing request: {}", ioException.getMessage());
			createdRating.setErrorMessage("Exception while parsing request: " + ioException.getMessage());
			return new ResponseEntity<>(createdRating, HttpStatus.OK);
		}
		createdRating = ratingAndReviewService.addRating(rating, baseUrl);
		if (createdRating.getErrorCode() != 0) {
			if (createdRating.getErrorCode() == 2000) {

				return new ResponseEntity<>(transformProcessor.transformTo(createdRating, RatingAndReview.class),
						HttpStatus.CONFLICT);
			}

			return new ResponseEntity<>(createdRating, HttpStatus.INTERNAL_SERVER_ERROR);
		} else {

			RatingAndReview createdRatingDTO = transformProcessor.transformTo(createdRating, RatingAndReview.class);
			return new ResponseEntity<>(createdRatingDTO, HttpStatus.OK);
		}
	}

	/********************************************
	 * API Users *
	 ********************************************/
	/**
	 * This API is used for getting the Datatable grid for the API RatingAndReview
	 * page.
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
	 * @return It will return grid for the API RatingAndReview page in Datatable
	 *         format.
	 */
	@GetMapping(value = "/api/get/reviews")
	public ResponseEntity<DataTableResponse> getReviews(@RequestParam(value = "offset", required = true) Integer offset,
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

			// logger.debug("account: {}", userProfile.getAccount().getId());

			// logger.debug("account: {}", userProfile.getAccount().getId());

			if ((null != column && !column.isEmpty()) && (null != dir && !dir.isEmpty())) {
				paginationSearchResult = ratingAndReviewService.findAllRatingForDataTable(
						PaginationSearchParams.buildSearchParams(dataTablesInput),
						OrderParams.getOrderParam(column, dir), null);
			} else {
				paginationSearchResult = ratingAndReviewService.findAllRatingForDataTable(
						PaginationSearchParams.buildSearchParams(dataTablesInput),
						OrderParams.buildOrderParams(dataTablesInput), null);
			}
			// convert to dto
			Function<RatingAndReview, RatingAndReview> toApiUserDTO = (entity) -> {

				RatingAndReview RatingAndReview = transformProcessor.transformTo(entity, RatingAndReview.class);

				return RatingAndReview;
			};
			List<RatingAndReview> resultDTOs = new ArrayList<>();
			paginationSearchResult.getResults().forEach(c -> resultDTOs.add(toApiUserDTO.apply((RatingAndReview) c)));

			dataTableResponse = new DataTableResponse(resultDTOs, paginationSearchResult.getRowCount(),
					paginationSearchResult.getRowCount(), Long.valueOf(dataTablesInput.getDraw()));

		} catch (Exception exception) {
			exception.printStackTrace();
			dataTableResponse = new DataTableResponse(new ArrayList<RatingAndReview>(), 1l, 0l, 0l);
			logger.error("Exception occurred while retreiving RatingAndReview table: {}", exception.getMessage());
		}
		return new ResponseEntity<>(dataTableResponse, HttpStatus.OK);
	}

	/**
	 * This API is used for getting the API RatingAndReview details on the basis of
	 * id.
	 * 
	 * @param id : Here pass the id for which you need API RatingAndReview details.
	 * @return It will return API RatingAndReview details which id is passed.
	 */
	@GetMapping(value = "/api/get/RatingAndReview/{id}")
	public ResponseEntity<RatingAndReview> getApiUser(@PathVariable long id) {
		try {
			// convert to dto
			Function<RatingAndReview, RatingAndReview> toApiUserDTO = (entity) -> {

				return transformProcessor.transformTo(entity, RatingAndReview.class);
			};

			RatingAndReview RatingAndReview = toApiUserDTO.apply(ratingAndReviewService.getRatingbyId(id));

			return new ResponseEntity<>(RatingAndReview, HttpStatus.OK);
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
	@PostMapping(value = "/api/create/RatingAndReview")
	public ResponseEntity<?> addUser(@RequestParam Map<String, Object> parameters, HttpServletRequest request,
			HttpServletResponse response) throws RatingAndReviewException {
		String baseUrl = request.getHeader("origin") + "/SetMyPassword/";
		RatingAndReview newApiRating = new RatingAndReview();
		String strRequest = null;
		RatingAndReview ratingAndReview = null;
		try {
			strRequest = objectMapper.writeValueAsString(parameters);
			ratingAndReview = objectMapper.readValue(strRequest, RatingAndReview.class);
		} catch (IOException ioException) {
			logger.error("Exception while parsing request: {}", ioException.getMessage());
			newApiRating.setErrorCode(400);
			newApiRating.setErrorMessage("Exception while parsing request: " + ioException.getMessage());
			return new ResponseEntity<>(new RatingAndReview(), HttpStatus.BAD_REQUEST);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		// convert RatingAndReview entity to RatingAndReview object
		Function<RatingAndReview, RatingAndReview> toUserDTO = (entity) -> transformProcessor.transformTo(entity,
				RatingAndReview.class);

		newApiRating = ratingAndReviewService.addRating(ratingAndReview, baseUrl);

		if (newApiRating.getErrorCode() != 0) {

			RatingAndReview newApiUserDTO = toUserDTO.apply(newApiRating);
			return new ResponseEntity<>(newApiUserDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		} else {

			RatingAndReview newApiUserDTO = toUserDTO.apply(newApiRating);
			return new ResponseEntity<>(newApiUserDTO, HttpStatus.OK);
		}
	}

	/********************************************
	 * Rating Users *
	 ********************************************/
	/**
	 * This API is used for getting the Average rating ProductNAme wise the API
	 * RatingAndReview page.
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
	 * @return It will return grid for the API RatingAndReview page in Datatable
	 *         format.
	 */
	@GetMapping(value = "/api/get/avrage/rating/reviews")
	public ResponseEntity<Object> getAverageReviewsAndRationg(

			@RequestParam(value = "productName", required = true) String productName, HttpServletRequest request,
			HttpServletResponse response) {
		Map<Object, Long> resultResponse = new HashMap<>();
		DataTablesInput dataTablesInput = new DataTablesInput();
		dataTablesInput.setDraw(1);
		dataTablesInput.setStart(null);
		dataTablesInput.setLength(null);
		if (null != productName || !productName.isEmpty()) {
			dataTablesInput.setSearch(new SearchParameter(productName, true));
		} else {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("Error", "productName is not null and empty");
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		dataTablesInput.setOrder(new ArrayList<>());

		PaginationSearchResult paginationSearchResult = null;
		DataTableResponse dataTableResponse = null;
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			paginationSearchResult = ratingAndReviewService.findAllRatingForDataTable(
					PaginationSearchParams.buildSearchParams(dataTablesInput),
					OrderParams.buildOrderParams(dataTablesInput), null);

			// convert to dto
			Function<RatingAndReview, RatingAndReview> toApiUserDTO = (entity) -> {

				RatingAndReview RatingAndReview = transformProcessor.transformTo(entity, RatingAndReview.class);

				return RatingAndReview;
			};
			List<RatingAndReview> resultDTOs = new ArrayList<>();
			paginationSearchResult.getResults().forEach(c -> resultDTOs.add(toApiUserDTO.apply((RatingAndReview) c)));

			resultResponse = resultDTOs.stream()
					.collect(Collectors.groupingBy(e -> e.getRating(), Collectors.counting()));
			return new ResponseEntity<>(resultResponse, HttpStatus.OK);
		} catch (Exception exception) {
			exception.printStackTrace();
			dataTableResponse = new DataTableResponse(new ArrayList<RatingAndReview>(), 1l, 0l, 0l);
			logger.error("Exception occurred while retreiving RatingAndReview table: {}", exception.getMessage());
		}
		return new ResponseEntity<>(resultResponse, HttpStatus.OK);
	}

	/********************************************
	 * Rating Users *
	 ********************************************/
	/**
	 * This API is used for getting the Average rating ProductNAme wise the API
	 * RatingAndReview page.
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
	 * @return It will return grid for the API RatingAndReview page in Datatable
	 *         format.
	 */
	@GetMapping(value = "/api/get/avrage/mothly/rating/reviews")
	public ResponseEntity<Object> getAverageMonthlyReviewsAndRationg(

			@RequestParam(value = "productName", required = true) String productName,
			@RequestParam(value = "reviewSource", required = true) String reviewSource,
			@RequestParam(value = "startDate", required = true) String startDate,
			@RequestParam(value = "endDate", required = true) String endDate, HttpServletRequest request,
			HttpServletResponse response) {
		Map<Object, Double> resultResponse = new HashMap<>();
		try {

			if (null == productName || productName.isEmpty()) {

				Map<String, String> errorResponse = new HashMap<>();
				errorResponse.put("Error", "productName is not null or empty");
				return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
			}
			if (null == reviewSource || reviewSource.isEmpty()) {
				Map<String, String> errorResponse = new HashMap<>();
				errorResponse.put("Error", "reviewSource is  null or empty");
				return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			Date d1 = sdf.parse(startDate);
			Date d2 = sdf.parse(endDate);
			long difference_In_Time = d2.getTime() - d1.getTime();

			long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;
			
			if (!(difference_In_Days>28)) {
				Map<String, String> errorResponse = new HashMap<>();
				errorResponse.put("Error", "Date span  is   less than a month");
				return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
			}
			
			List<RatingAndReview> mothlyReviewData = ratingAndReviewService.getReviewDataBetweenDates(startDate,
					endDate, productName, reviewSource);
			double averageRating =mothlyReviewData.stream()
	                .mapToDouble(d -> d.getRating())
	                .average()
	                .orElse(0.0);
			resultResponse.put("averageRating", averageRating);
			return new ResponseEntity<>(resultResponse, HttpStatus.OK);

		} catch (Exception exception) {
			exception.printStackTrace();

			logger.error("Exception occurred while retreiving RatingAndReview table: {}", exception.getMessage());
		}
		return new ResponseEntity<>(resultResponse, HttpStatus.OK);
	}

}
