package com.rating.bo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.rating.bo.common.BaseEntity;

/**
 * @author Ankita Shrothi
 *
 */
@Entity
@Table(name = "token_manager")

public class TokenManager extends BaseEntity {

	private static final long serialVersionUID = -8307201575545925513L;

	@Column(name = "TOKEN_VALUE", nullable = false)
	private String tokenValue;
	
	@Column(name = "EXPIRY_DATE", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiryDate;

	/**
	 * @return the tokenValue
	 */
	public String getTokenValue() {
		return tokenValue;
	}

	/**
	 * @param tokenValue the tokenValue to set
	 */
	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}

	/**
	 * @return the expiryDate
	 */
	public Date getExpiryDate() {
		return expiryDate;
	}

	/**
	 * @param expiryDate the expiryDate to set
	 */
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
}