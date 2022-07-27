package com.rating.bo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import com.rating.bo.common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_details")

@DynamicUpdate
@Getter
@Setter
public class UserDetails extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "ATTEMPTS")
	private int attempts;

	@Column(name = "USER_ACTION")
	private int userAction;

	@Column(name = "ACTION_DATE")
	private Date actionDate;

	@Column(name = "SECURITY_KEY")
	private String securityKey;

	@Column(name = "OTP_COUNT")
	private Integer otpCount;

	@Column(name = "PASSWORD_HISTORY")
	private String passwordHistory;

	/**
	 * Setter for PasswordHistory with json String argument.
	 * 
	 * @param passwordHistory
	 */
	public void setPasswordHistory(String passwordHistory) {
		this.passwordHistory = passwordHistory;
	}

	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	public int getUserAction() {
		return userAction;
	}

	public void setUserAction(int userAction) {
		this.userAction = userAction;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public String getSecurityKey() {
		return securityKey;
	}

	public void setSecurityKey(String securityKey) {
		this.securityKey = securityKey;
	}

	public Integer getOtpCount() {
		return otpCount;
	}

	public void setOtpCount(Integer otpCount) {
		this.otpCount = otpCount;
	}

	public String getPasswordHistory() {
		return passwordHistory;
	}
	
	
	


}
