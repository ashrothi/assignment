package com.rating.business.logic;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 
 * @author Ankita Shrothi
 *
 */

public interface CustomUserDetailsService extends UserDetailsService {
	/*
	 * It will return User Details on the basis of passed user name
	 * 
	 * @param username- User Name of the user
	 * 
	 * @return UserDetails
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

	

}
