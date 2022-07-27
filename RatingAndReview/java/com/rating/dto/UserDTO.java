package com.rating.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rating.dto.common.AbstractItem;
/**
 * @author  Ankita Shrothi  // modified
 *
 * */
public class UserDTO extends AbstractItem {

	private static final long serialVersionUID = -5567972585187856060L;

	private String userName;

	@JsonIgnore
	private String password;


	private boolean locked;

	
	
	private String currentPassword;

	private String confirmPassword;
	
	private String flag;


	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	@Override
	public String toString() {
		return "User [UserName=" + userName + "]";
	}
}
