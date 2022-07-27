package com.rating.dto;

public class LoggedInUserDto {

	private Long loggedInUserId;

	private Long loggedInAccountId;

	private String loggedInUserName;

	public Long getLoggedInUserId() {
		return loggedInUserId;
	}

	public void setLoggedInUserId(Long loggedInUserId) {
		this.loggedInUserId = loggedInUserId;
	}

	public Long getLoggedInAccountId() {
		return loggedInAccountId;
	}

	public void setLoggedInAccountId(Long loggedInAccountId) {
		this.loggedInAccountId = loggedInAccountId;
	}

	public String getLoggedInUserName() {
		return loggedInUserName;
	}

	public void setLoggedInUserName(String loggedInUserName) {
		this.loggedInUserName = loggedInUserName;
	}

}
