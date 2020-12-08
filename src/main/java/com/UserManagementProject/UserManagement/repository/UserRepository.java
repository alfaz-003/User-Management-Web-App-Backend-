package com.UserManagementProject.UserManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.UserManagementProject.UserManagement.domain.User;

public interface UserRepository extends JpaRepository<User,Long>{
	
	
	User findUserByUsername(String username);
	
	User findUserByEmail(String email); 

}
