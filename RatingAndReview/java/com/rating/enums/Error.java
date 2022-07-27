package com.rating.enums;

public enum Error {

	UNDEFINED(0, "Error code undefined"),

	// Server errors/integration errors
	ServerError(100, "A unexpected error occured, please try again or contact  support, support@rating.com"),
	InvalidCredentials(101, "Invalid password"), InvalidToken(102, "Invalid token"), ExpiredToken(103, "Expired token"),
	RestrictedOperation(104, "Restricted operation performed"),

	// User (HTTP/WS) Session errors
	SessionExpiredError(200, "The session expired, please log on again"),

	// Session errors

	// Duplicate errors
	DuplicateEmailError(400, "There is already a user with this email registered"),

	// Required errors
	RequiredNameError(500, "Please enter a correct name"),

	// Errors
	AccessDeniedError(900, "Access denied error");

	private int code;
	public String errorMessage;

	public int getCode() {
		return code;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	private Error(int code, String message) {
		this.code = code;
		this.errorMessage = message;
	}

	public static Error fromCode(int code) {
		for (Error entry : values()) {
			if (entry.getCode() == code) {
				return entry;
			}
		}
		return null;
	}
}
