package com.UserManagementProject.UserManagement.service;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.stereotype.Service;

import com.UserManagementProject.UserManagement.domain.User;
import com.UserManagementProject.UserManagement.exception.domain.EmailExistsException;
import com.UserManagementProject.UserManagement.exception.domain.UserNotFoundException;
import com.UserManagementProject.UserManagement.exception.domain.UsernameExistsException;

@Service
public interface UserService {
	
	User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistsException, EmailExistsException, MessagingException;
	
	List<User> getUsers();
	
	User findUserByUsername(String username);
	
	User findUserByEmail(String email);

}
