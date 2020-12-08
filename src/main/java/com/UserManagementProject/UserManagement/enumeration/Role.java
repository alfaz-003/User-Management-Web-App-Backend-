package com.UserManagementProject.UserManagement.enumeration; 
import static com.UserManagementProject.UserManagement.constant.Authority.USER_AUTHORITIES;
import static com.UserManagementProject.UserManagement.constant.Authority.HR_AUTHORITIES;
import static com.UserManagementProject.UserManagement.constant.Authority.ADMIN_AUTHORITIES;
import static com.UserManagementProject.UserManagement.constant.Authority.SUPER_ADMIN_AUTHORITIES;
import static com.UserManagementProject.UserManagement.constant.Authority.MANAGER_AUTHORITIES;



public enum Role {
	
	ROLE_USER(USER_AUTHORITIES),
	ROLE_HR(HR_AUTHORITIES),
	ROLE_ADMIN(ADMIN_AUTHORITIES),
	ROLE_MANAGER(MANAGER_AUTHORITIES),
	ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);
	
	private String[] authorities;
	
	Role(String...authorities) {
		this.authorities = authorities;
		
	}
	
	public String[] getAuthorities() {
		return authorities; 
	}
	

}
