package com.rating.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rating.dto.common.AbstractItem;

import java.util.Date;

public class ApiUserDTO extends AbstractItem {

    private String userName;

    private String firstName;

    private String lastName;

    private String email;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String token;

    private boolean locked;

    private boolean deleted;

    private Date deleteDate;

    private Date tokenExpiryDate;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    

    public Date getTokenExpiryDate() {
        return tokenExpiryDate;
    }

    public void setTokenExpiryDate(Date tokenExpiryDate) {
        this.tokenExpiryDate = tokenExpiryDate;
    }
    
    @Override
	public String toString() {
		return "ApiUser [UserName=" + userName + "]";
	}
}
