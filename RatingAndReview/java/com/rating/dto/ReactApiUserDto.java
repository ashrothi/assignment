package com.rating.dto;

import com.rating.dto.common.AbstractItem;

public class ReactApiUserDto extends AbstractItem {

	private static final long serialVersionUID = -5308440067682672702L;
	
	private String userName;
	private String firstName;
	private String lastName;
	private String emailId;
	private Boolean locked;
	private Long accountId;
	private String apiMappingIds;
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
	 * @return the emailAddress
	 */
	public String getEmailId() {
		return emailId;
	}
	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	/**
	 * @return the locked
	 */
	public Boolean isLocked() {
		return locked;
	}
	/**
	 * @param locked the locked to set
	 */
	public void setLocked(Boolean locked) {
		this.locked = locked;
	}
	/**
	 * @return the accountId
	 */
	public Long getAccountId() {
		return accountId;
	}
	/**
	 * @param accountId the accountId to set
	 */
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	/**
	 * @return the apiMappingIds
	 */
	public String getApiMappingIds() {
		return apiMappingIds;
	}
	/**
	 * @param apiMappingIds the apiMappingIds to set
	 */
	public void setApiMappingIds(String apiMappingIds) {
		this.apiMappingIds = apiMappingIds;
	}

}
