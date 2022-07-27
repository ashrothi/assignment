package com.rating.bo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Where;

import com.rating.bo.common.BaseEntity;

/**
 * @author Ankita Shrothi
 *
 */
@Entity
@Table(name = "rating")

public class RatingAndReview extends BaseEntity {

	private static final long serialVersionUID = -8307201575547925513L;

	@Column(name = "review", nullable = true)
	private String review;

	@NotNull(message = "author is required")
	@Column(name = "author", nullable = false)
	private String author;

	@NotNull(message = "Review Source is required")
	@Column(name = "review_source", nullable = false)
	private String reviewSource;

	@NotNull(message = "rating is required")
	@Column(name = "rating", nullable = false)
	private Integer rating;

	@Column(name = "title")
	private String title;
	@NotNull(message = "Product Name is required")
	@Column(name = "product_name")
	private String productName;

	@Column(name = "reviewed_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date reviewed_date;

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getReviewSource() {
		return reviewSource;
	}

	public void setReviewSource(String reviewSource) {
		this.reviewSource = reviewSource;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Date getReviewed_date() {
		return reviewed_date;
	}

	public void setReviewed_date(Date reviewed_date) {
		this.reviewed_date = reviewed_date;
	}

	

	

}