package com.rating.business.logic;

import java.util.List;
import java.util.Map;

import com.rating.dto.ApiResponse;
import com.rating.dto.ReactApiUserDto;
import com.rating.utils.order.OrderParams;
import com.rating.bo.ApiUser;
import com.rating.bo.RatingAndReview;
import com.rating.bo.User;
import com.rating.utils.search.PaginationSearchParams;
import com.rating.utils.search.PaginationSearchResult;

/**
 * @author Ankita Shrothi
 *
 */
public interface RatingAndReviewService extends CrudService {

	/**
	 * @param params
	 * @return
	 */
	ApiResponse getRatings(Map<String, Object> params);

	/**
	 * @param params
	 * @return
	 */
	ApiResponse getRating(Map<String, Object> params);

	

	
	/**
	 * @param params
	 * @return
	 */
	ApiResponse createOrUpdateRating(Map<String, Object> params);

	/**
	 * @param params
	 * @return
	 */
	ApiResponse deleteRating(Map<String, Object> params);





	/**
	 * @param ApiUsers
	 * @return
	 */
	public PaginationSearchResult findAllRatingForDataTable(PaginationSearchParams paginationSearchParams,
			OrderParams orderparams, User userProfile);

	
	
	
	
	public RatingAndReview addRating(RatingAndReview ratingAndReviewToAdd, String baseUrl);
	
	public RatingAndReview updateRating(long ratingAndReviewId, RatingAndReview ratingAndReview);

	
	
	public void deleteRating(RatingAndReview ratingAndReview);

	RatingAndReview getRatingbyId(long id);

	List<RatingAndReview> getReviewDataBetweenDates(String startDate, String endDate, String productName,
			String reviewSource);
}
