package com.rating.dto.common;

import java.io.Serializable;
import java.util.Date;

import org.javers.core.metamodel.annotation.DiffIgnore;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.ALWAYS)
public class AbstractItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Long id;

	private long errorCode;

	private String errorMessage;

	@DiffIgnore
	private Date createDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(long errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
