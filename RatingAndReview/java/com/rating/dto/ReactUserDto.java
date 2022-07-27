package com.rating.dto;

/**
 * Api request body pojo representation for User CRUD apis.
 *
 */
public class ReactUserDto {
	private static final long serialVersionUID = -5567972586587856060L;
	private Long userId;
	private String userName;
	private String userType;
	private String firstName;
	private String lastName;
	private String primaryPhone;
	private String secondaryPhone;
	private String email;
	private String emailConf;
	private Boolean locked;
	private Long accountId;
	private Long groupId;
	private Long roleId;
	private Long timeZoneId;
	private Long countryId;
	private Integer tutorialAction;

	private String userAccountType;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPrimaryPhone() {
		return primaryPhone;
	}

	public void setPrimaryPhone(String primaryPhone) {
		this.primaryPhone = primaryPhone;
	}

	public String getSecondaryPhone() {
		return secondaryPhone;
	}

	public void setSecondaryPhone(String secondaryPhone) {
		this.secondaryPhone = secondaryPhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailConf() {
		return emailConf;
	}

	public void setEmailConf(String emailConf) {
		this.emailConf = emailConf;
	}

	public Boolean isLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(Long timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public Boolean getLocked() {
		return locked;
	}

	public Integer getTutorialAction() {
		return tutorialAction;
	}

	public void setTutorialAction(Integer tutorialAction) {
		this.tutorialAction = tutorialAction;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getUserAccountType() {
		return userAccountType;
	}

	public void setUserAccountType(String userAccountType) {
		this.userAccountType = userAccountType;
	}

}
