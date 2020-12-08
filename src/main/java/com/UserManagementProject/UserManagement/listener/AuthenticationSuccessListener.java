package com.UserManagementProject.UserManagement.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.UserManagementProject.UserManagement.domain.UserPrincipal;
import com.UserManagementProject.UserManagement.service.LoginAttemptService;

@Component
public class AuthenticationSuccessListener {
	
	private LoginAttemptService loginAttemptService;

	
	@Autowired
	public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
		super();
		this.loginAttemptService = loginAttemptService;
	}
	
	
	@EventListener
	public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
		Object principal = event.getAuthentication().getPrincipal();
		if(principal instanceof UserPrincipal) {
			UserPrincipal user = (UserPrincipal) event.getAuthentication().getPrincipal();
			loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
			
		}
	}
	
	

}
