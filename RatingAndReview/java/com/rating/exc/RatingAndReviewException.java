package com.rating.exc;
/**
 * 
 * @author Ankita Shrothi
 * To handle portal exception.
 */
public class RatingAndReviewException extends Exception {

	public RatingAndReviewException() {
		super();
	}

	public RatingAndReviewException(String message) {
		super(message);
	}

	public RatingAndReviewException(Throwable exception) {
		super(exception);
	}

	public RatingAndReviewException(String message, Throwable exception) {
		super(message, exception);
	}

}
