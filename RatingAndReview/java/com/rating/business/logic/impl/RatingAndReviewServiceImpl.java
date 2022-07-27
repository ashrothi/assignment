package com.rating.business.logic.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
import com.rating.bo.RatingAndReview;
import com.rating.bo.RatingAndReview;
import com.rating.bo.RatingAndReview;
import com.rating.bo.RatingAndReview;
import com.rating.bo.User;
import com.rating.business.logic.ApiUserService;
import com.rating.business.logic.CommonService;
import com.rating.business.logic.RatingAndReviewService;
import com.rating.business.logic.ServiceCommonUtils;
import com.rating.utils.search.PaginationSearchParams;
import com.rating.utils.search.PaginationSearchResult;

/**
 * @author Ankita Shrothi
 *
 */

/***
 * This class is used to create API user ,Suspend API User,Resume API
 * User,Update API User and Get Details API users Also used for Generate
 * Password,change Password,forget Password
 *
 */
@Service
@Transactional("RatingAndReviewTransactionManager")
public class RatingAndReviewServiceImpl extends CrudServiceImpl implements RatingAndReviewService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CrudRepository crudRepository;

	@Autowired
	private Gson gson;

	@Resource
	private Environment env;

	@Autowired
	private Validator validator;

	@Autowired(required = true)
	private CommonService commonService;

	@Override
	public ApiResponse getRatings(Map<String, Object> params) {
		ServiceCommonUtils.generateUniqueIdByDateTime();

		Repository.ComplexQueryAttribute complexQueryAttribute = Repository.ComplexQueryAttribute
				.newInstance(Repository.ComplexQueryMode.OR);

		CrudRepository.QueryFilter<RatingAndReview> queryFilter = CrudRepository.QueryFilter
				.newInstance(RatingAndReview.class);

		queryFilter = QueryUtils.buildPagingAndSortingFilter(queryFilter, params, RatingAndReview.class);
		queryFilter.addComplexQueryAttribute(complexQueryAttribute);
		List<RatingAndReview> users = crudRepository.getEntityEntries(queryFilter);
		return new ApiResponse(HttpStatus.OK.value(), users, crudRepository.countEntityEntries(queryFilter).intValue(),
				users.size());
	}

	@Override
	public ApiResponse getRating(Map<String, Object> params) {
		Long ratingId = Long.parseLong(params.get("id").toString());
		RatingAndReview rating = crudRepository.getEntityById(RatingAndReview.class, ratingId);

		if (rating == null) {
			logger.error("rating not found : " + params);
			return new ApiResponse("Rating Not Found");
		}
		return new ApiResponse(HttpStatus.OK.value(), rating);
	}

	@Override
	public ApiResponse createOrUpdateRating(Map<String, Object> params) {
		ServiceCommonUtils.generateUniqueIdByDateTime();

		logger.error("Update user  : " + params);
		if (params == null || params.size() == 0) {
			return new ApiResponse("Empty Request");
		}
		try {
			JsonElement jsonElement = gson.toJsonTree(params);
			RatingAndReview ratingAndReview = gson.fromJson(jsonElement, RatingAndReview.class);
			params.get("loggedInAccountId");

			if (ratingAndReview.getId() == null) {

				Set<ConstraintViolation<RatingAndReview>> constraintViolations = validator.validate(ratingAndReview);
				if (constraintViolations.size() == 0) {

					crudRepository.createEntity(RatingAndReview.class, ratingAndReview);

					return new ApiResponse(HttpStatus.CREATED.value());
				} else {
					List<String> errorMessages = new ArrayList<String>();
					for (ConstraintViolation<RatingAndReview> constraintViolation : constraintViolations) {
						errorMessages.add(constraintViolation.getMessage());
					}

					return new ApiResponse(HttpStatus.BAD_REQUEST.value(), StringUtils.join(errorMessages, ","));
				}

			} else {
				getEntityById(RatingAndReview.class, ratingAndReview.getId());

				Set<ConstraintViolation<RatingAndReview>> constraintViolations = validator.validate(ratingAndReview);
				if (constraintViolations.size() == 0) {
					crudRepository.mergeEntity(RatingAndReview.class, ratingAndReview);
					return new ApiResponse(HttpStatus.NO_CONTENT.value());
				} else {
					List<String> errorMessages = new ArrayList<String>();
					for (ConstraintViolation<RatingAndReview> constraintViolation : constraintViolations) {
						errorMessages.add(constraintViolation.getMessage());
					}
					return new ApiResponse(HttpStatus.BAD_REQUEST.value(), StringUtils.join(errorMessages, ","));
				}
			}
		} catch (Exception e) {
			logger.error("Unable to rating and review  : " + e.getMessage());
			return new ApiResponse("Unable to rating and review ");

		}

	}

	@Override
	public ApiResponse deleteRating(Map<String, Object> params) {
		Long ratingId = Long.parseLong(params.get("id").toString());

		RatingAndReview ratingAndReview = crudRepository.getEntityById(RatingAndReview.class, ratingId);

		if (ratingAndReview == null) {
			logger.error("Rating not found : " + params);
			return new ApiResponse("Rating not found :");
		}

		try {

			crudRepository.deleteEntity(RatingAndReview.class, ratingId);
		} catch (Exception e) {
			logger.error("Unable to delete the Rating : " + e.getMessage());
			return new ApiResponse("Unable to delete the Rating :");
		}
		return new ApiResponse(HttpStatus.NO_CONTENT.value());
	}

	@Override
	public PaginationSearchResult findAllRatingForDataTable(PaginationSearchParams paginationSearchParams,
			OrderParams orderParams, User userProfile) {
		return new QueryFilterTemplate<PaginationSearchParams>(this.crudRepository, paginationSearchParams, null) {

			public CrudRepository.QueryFilter buildSearchFilter(PaginationSearchParams searchContainer) {

				Repository.ComplexQueryAttribute complexQueryAttribute = Repository.ComplexQueryAttribute
						.newInstance(Repository.ComplexQueryMode.OR);

				CrudRepository.QueryFilter<RatingAndReview> queryFilter = CrudRepository.QueryFilter
						.newInstance(RatingAndReview.class);

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
						Repository.ComplexQueryAttribute searchQueryAttribute = commonService
								.searchTableData("RatingAndReview", searchContainer.getSearchValue());
						queryFilter.addComplexQueryAttribute(searchQueryAttribute);
					} catch (RatingAndReviewException e) {
						logger.error("Error while getting API Users : {}", ExceptionUtils.getStackTrace(e));
					}
				}
				return queryFilter;
			}
		}.load(RatingAndReview.class);
	}

	@Override
	public RatingAndReview addRating(RatingAndReview ratingAndReviewToAdd, String baseUrl) {
		RatingAndReview ratingAndReview = new RatingAndReview();
		try {

			Date date = new Date();
			SimpleDateFormat DateFor = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
			String stringDate = DateFor.format(date);
			ratingAndReview.setAuthor(ratingAndReviewToAdd.getAuthor());
			ratingAndReview.setProductName(ratingAndReviewToAdd.getProductName());
			ratingAndReview.setReviewSource(ratingAndReviewToAdd.getReviewSource());

			ratingAndReview.setRating(ratingAndReviewToAdd.getRating());
			ratingAndReview.setReview(ratingAndReviewToAdd.getReview());
			ratingAndReview.setTitle(ratingAndReviewToAdd.getTitle());
			ratingAndReview.setReviewed_date(DateFor.parse(stringDate));
			RatingAndReview createdUser = crudRepository.createEntity(RatingAndReview.class, ratingAndReview);
			logger.debug("Created new Api User with ID : {}", createdUser.getId());

			

			return createdUser;
		} catch (Exception e) {
			logger.error("Exception while API User creation:{}", e.getMessage());
			ratingAndReview.setErrorCode(2000);
			ratingAndReview.setErrorMessage("Error in Creating Rating");
			return ratingAndReview;
		}
	}

	@Override
	public RatingAndReview updateRating(long ratingAndReviewId, RatingAndReview ratingAndReview) {
		RatingAndReview original = getRatingbyId(ratingAndReviewId);
		try {
			
			original.setProductName(ratingAndReview.getProductName());
		

			original.setRating(ratingAndReview.getRating());
			original.setReview(ratingAndReview.getReview());
			original.setTitle(ratingAndReview.getTitle());

		
			return crudRepository.mergeEntity(RatingAndReview.class, original);
		} catch (Exception e) {
			logger.error("Exception while updating api user:{}", e.getMessage());
			return null;
		}
	}
	@Override
	public RatingAndReview getRatingbyId(long ratingAndReviewId) {
		Repository.ComplexQueryAttribute complexQueryAttribute = Repository.ComplexQueryAttribute
				.newInstance(Repository.ComplexQueryMode.OR);
		complexQueryAttribute.addQueryAttribute(
				Repository.QueryAttribute.newInstance("id", ratingAndReviewId, Repository.QueryMode.EQ, Long.class));
		return crudRepository.getSingleEntity(
				CrudRepository.QueryFilter.newInstance(RatingAndReview.class).addComplexQueryAttribute(complexQueryAttribute));
	}

	@Override
	public void deleteRating(RatingAndReview ratingAndReview) {
		try {
			
			crudRepository.deleteEntity(RatingAndReview.class, ratingAndReview.getId());

			
		} catch (Exception e) {
			logger.error("Unable to delete the user : {}", e.getMessage());
		}
	}

	@Override
	public List<RatingAndReview> getReviewDataBetweenDates(String startDate, String endDate, String productName,
			String reviewSource) {
		Repository.ComplexQueryAttribute complexQueryAttribute = Repository.ComplexQueryAttribute
				.newInstance(Repository.ComplexQueryMode.OR);
		complexQueryAttribute.addQueryAttribute(
				Repository.QueryAttribute.newInstance("productName", productName, Repository.QueryMode.EQ, Long.class));
		complexQueryAttribute.addQueryAttribute(
				Repository.QueryAttribute.newInstance("reviewSource", reviewSource, Repository.QueryMode.EQ, Long.class));
		complexQueryAttribute.addQueryAttribute(
				Repository.QueryAttribute.newInstance("reviewed_date", startDate, Repository.QueryMode.GE, Long.class));
		
		complexQueryAttribute.addQueryAttribute(
				Repository.QueryAttribute.newInstance("reviewed_date", endDate, Repository.QueryMode.LE, Long.class));
		
		return crudRepository.getEntityEntries(
				CrudRepository.QueryFilter.newInstance(RatingAndReview.class).addComplexQueryAttribute(complexQueryAttribute));

	}

}
