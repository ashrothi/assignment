package com.rating.bo.common;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.javers.core.metamodel.annotation.DiffIgnore;

@MappedSuperclass
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = -4842358190664392964L;

	/**
	 * Persistence key
	 */
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "CREATE_DATE", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@DiffIgnore
	private Date createDate;

	@Transient
	private long errorCode;

	@Transient
	private String errorMessage;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BaseEntity() {
		this.setCreateDate(new Date());
		this.errorCode = 0;
		this.errorMessage = null;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!getClass().getName().split("_")[0].equals(obj.getClass().getName().split("_")[0])) {
			return false;
		}
		// if (getClass() != obj.getClass()) {
		// return false;
		// }
		BaseEntity other = (BaseEntity) obj;
		if (id == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!id.equals(other.getId())) {
			return false;
		}
		return true;
	}
}
