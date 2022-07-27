package com.rating.bo;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rating.bo.common.BaseEntity;


@Entity
@Table(name = "users")

@Where(clause = "DELETED = 0")
public class User extends BaseEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8307201575547925513L;

	@Column(name = "USER_NAME",nullable=false)
    private String userName;

    @Column(name = "PASSWORD")
    private String password;



    @Column(name = "LOCKED",nullable=false)
    private boolean locked;

    
    @Column(name = "DELETED")
    private boolean deleted;

    @Column(name = "DELETED_DATE", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
	private Date deleteDate;

   
   

	@JsonIgnore
	@Column(name = "ATTEMPTS")
	private int attempts;

	@JsonIgnore
	@Column(name = "LAST_ACTIVE")
	private Date lastActive;

    @Column(name = "USER_ACTION")
    @JsonIgnore
    private int userAction;
    
	@JsonIgnore
	@Column(name = "SECURITY_KEY")
	private String securityKey;
    
	//To change password
	@Transient
	private String currentPassword;
	
	@Transient
	private String confirmPassword;
	
	@Transient
	@JsonIgnore
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

   

	public int getUserAction() {
		return userAction;
	}

	public void setUserAction(int userAction) {
		this.userAction = userAction;
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
	
	

	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	public Date getLastActive() {
		return lastActive;
	}

	public void setLastActive(Date lastActive) {
		this.lastActive = lastActive;
	}

	@Override
	public String toString() {
		return userName;
	}

	public String getSecurityKey() {
		return securityKey;
	}

	public void setSecurityKey(String securityKey) {
		this.securityKey = securityKey;
	}

	public User getUserDetails() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
