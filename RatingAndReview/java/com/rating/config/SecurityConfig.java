package com.rating.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.web.filter.GenericFilterBean;

import com.rating.business.logic.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = "com.rating")
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder authenticationMgr) throws Exception {
		authenticationMgr.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());		
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		//add your custom encoding filter as the first filter in the chain
        http.addFilterBefore(new EncodingFilter(), ChannelProcessingFilter.class);

       http
        .csrf().disable()
        .authorizeRequests()
        	.antMatchers(HttpMethod.GET, "/autoLogin")
        	.permitAll()
            .antMatchers("/globeconnect/**").fullyAuthenticated()
            .and()
        .formLogin()
            .loginPage("/login")
            .permitAll()
            .defaultSuccessUrl("/loginSuccess",true)
            .failureUrl("/loginFailure")
            .and()
            .headers()
			.frameOptions().sameOrigin()
			.httpStrictTransportSecurity().disable()
       		.and().exceptionHandling().accessDeniedPage("/access-denied.html");
	}
	
	@Bean(name = "authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
          return super.authenticationManagerBean();
    }

}

class EncodingFilter extends GenericFilterBean {

    @Override
    public void doFilter(
            ServletRequest request, 
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        chain.doFilter(request, response);
    }
}
