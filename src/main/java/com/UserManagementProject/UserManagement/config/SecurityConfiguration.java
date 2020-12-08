package com.UserManagementProject.UserManagement.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import com.UserManagementProject.UserManagement.constant.SecurityConstant;
import com.UserManagementProject.UserManagement.filter.JwtAccessDeniedhandler;
import com.UserManagementProject.UserManagement.filter.JwtAuthenticationEntryPoint;
import com.UserManagementProject.UserManagement.filter.JwtAuthorizationFilter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)



public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired 
	private UserDetailsService userDetailsService;

	
	private JwtAuthorizationFilter jwtAuthorizationFilter;
	private JwtAccessDeniedhandler jwtAccessDeniedhandler;
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	
	@Autowired
	
	public SecurityConfiguration(JwtAuthorizationFilter jwtAuthorizationFilter,
			JwtAccessDeniedhandler jwtAccessDeniedhandler, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
			@Qualifier("UserDetailsService")UserDetailsService userDetailsService, 
			BCryptPasswordEncoder bCryptPasswordEncoder) {
		super();
		this.jwtAuthorizationFilter = jwtAuthorizationFilter;
		this.jwtAccessDeniedhandler = jwtAccessDeniedhandler;
		this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
		this.userDetailsService = userDetailsService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
		
		
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf().disable().cors().and()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and().authorizeRequests().antMatchers(SecurityConstant.PUBLIC_URLS).permitAll()
		.anyRequest().authenticated()
		.and()
		.exceptionHandling().accessDeniedHandler(jwtAccessDeniedhandler)
		.authenticationEntryPoint(jwtAuthenticationEntryPoint)
		.and()
		.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
		
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
		
	}
	
	

}
