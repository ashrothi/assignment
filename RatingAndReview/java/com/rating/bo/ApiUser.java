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
@Table(name = "api_users")
@Where(clause = "DELETED = 0")
public class ApiUser extends BaseEntity {

	private static final long serialVersionUID = -8307201575547925513L;

	@NotNull(message="userName is required")
	@Column(name = "USER_NAME", nullable = false)
	private String userName;
	
	@NotNull(message="firstName is required")
	@Column(name = "FIRST_NAME", nullable = false)
	private String firstName;
	
	@NotNull(message="lastName is required")
	@Column(name = "LAST_NAME", nullable = false)
	private String lastName;
	
	@NotNull(message="email is required")
	@Column(name = "EMAIL", nullable = false)
	private String email;


	@Column(name = "PASSWORD")
	private String password;
	
	@Column(name = "TOKEN")
	private String token;
	

	@Column(name = "LOCKED", nullable = false)
	private boolean locked;

	@Column(name = "DELETED")
	private boolean deleted;

	@Column(name = "DELETED_DATE", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date deleteDate;


	
	
	
	@Column(name = "TOKEN_EXPIRY_DATE", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date tokenExpiryDate;

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the locked
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * @param locked the locked to set
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * @return the deleted
	 */
	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * @param deleted the deleted to set
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	/**
	 * @return the deleteDate
	 */
	public Date getDeleteDate() {
		return deleteDate;
	}

	/**
	 * @param deleteDate the deleteDate to set
	 */
	public void setDeleteDate(Date deleteDate) {
		this.deleteDate = deleteDate;
	}

	

	/**
	 * @return the tokenExpiryDate
	 */
	public Date getTokenExpiryDate() {
		return tokenExpiryDate;
	}

	/**
	 * @param tokenExpiryDate the tokenExpiryDate to set
	 */
	public void setTokenExpiryDate(Date tokenExpiryDate) {
		this.tokenExpiryDate = tokenExpiryDate;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

	

}