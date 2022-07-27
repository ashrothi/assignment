package com.rating.dto;

import javax.validation.constraints.NotNull;

/**
 * The Class AuthRequest.
 */
public class AuthRequest {

	/** The username. */
	@NotNull(message = "username can't be null")
	private String username;

	/** The password. */
	@NotNull(message = "password can't be null")
	private String password;

	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username.
	 *
	 * @param username
	 *            the new username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 *
	 * @param password
	 *            the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}