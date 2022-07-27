package com.rating.business.logic.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.rating.exc.RatingAndReviewException;
import com.rating.business.logic.CustomUserDetailsService;
import com.rating.business.logic.UserService;

@Component
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

	@Autowired
	private UserService userService;

	/*
	 * It will return User Details on the basis of passed user name
	 * 
	 * @param username- User Name of the user
	 * 
	 * @return UserDetails
	 * 
	 * @see
	 * com.rating.bl.CustomUserDetailsService#loadUserByUsername(java.
	 * lang.String)
	 */
	public UserDetails loadUserByUsername(String userNameWithSecurityCode) throws UsernameNotFoundException {

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		// Sending userName appended with security code with "###". Hence splitting
		String[] userNameWithSecurityCodeArray = userNameWithSecurityCode.split("###");
		String userNameOrEmailId = userNameWithSecurityCodeArray[0];
		String securityCode = null;
		if (userNameWithSecurityCodeArray.length > 1) {
			securityCode = userNameWithSecurityCodeArray[1];
		}

		com.rating.bo.User prof = null;
		User user = null;
		try {
			/*
			 * get user details
			 */
			prof = userService.getUserByUserNameOrEmail(userNameOrEmailId);

			if ((prof == null)) {
				/* Profile not found */
				throw new BadCredentialsException("Invalid Username or Password");
			} else if (prof.isLocked()) {
				accountNonLocked = false;
			} else if (securityCode != null && !securityCode.isEmpty() && !securityCode.equals(prof.getUserDetails().getSecurityKey())) {
				throw new BadCredentialsException("Invalid security code");
			}
			// get user by passing params
			user = new User(userNameOrEmailId, prof.getPassword(), enabled, accountNonExpired, credentialsNonExpired,
					accountNonLocked, null );

		} catch (RatingAndReviewException exception) {

			exception.printStackTrace();
		}

		return user;
	}

	
/**
 * Not in use
 * 
 * It was used from spring UI methods 
 * @param roles
 * @return
 */
	public static List getGrantedAuthorities(List<String> roles) {
		List authorities = new ArrayList();

		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		return authorities;
	}
}
