/**
 * 
 */
package com.rating.business.logic;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Ankita Shrothi
 *
 */
@Transactional("RatingAndReviewTransactionManager")
public class IoTSecurityEventListener implements ApplicationListener<ApplicationEvent>, LogoutHandler, LogoutSuccessHandler
{
	

	
	public IoTSecurityEventListener() {
		
	}
	
	public IoTSecurityEventListener(SessionFactory sessionFactory) {
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.security.web.authentication.logout.LogoutHandler#logout(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.Authentication)
	 */
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		// TODO Auto-generated method stub
		System.out.println("User " +authentication.getName() +" Logged out");
	}

	public void onApplicationEvent(ApplicationEvent event) {

		if (event instanceof ContextClosedEvent) {
			if (event.getSource().toString().startsWith("WebApplicationContext")) {
			
			}
		} else if (event instanceof ContextRefreshedEvent) {
			
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.web.authentication.logout.LogoutSuccessHandler#onLogoutSuccess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.Authentication)
	 */
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		System.out.println("User " +authentication.getName() +" Logged out");		
	}
}
