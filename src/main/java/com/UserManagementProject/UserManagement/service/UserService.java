package com.UserManagementProject.UserManagement.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.UserManagementProject.UserManagement.domain.User;
import com.UserManagementProject.UserManagement.exception.domain.EmailExistsException;
import com.UserManagementProject.UserManagement.exception.domain.EmailNotFoundException;
import com.UserManagementProject.UserManagement.exception.domain.UserNotFoundException;
import com.UserManagementProject.UserManagement.exception.domain.UsernameExistsException;

@Service
public interface UserService {
	
	User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistsException, EmailExistsException, MessagingException;
	
	List<User> getUsers();
	
	User findUserByUsername(String username);
	
	User findUserByEmail(String email);
	
	User addNewUser(String firstName, String lastName, String username, String email,String role, boolean isNotLocked, boolean isActive , MultipartFile profileImage) throws UserNotFoundException, UsernameExistsException, EmailExistsException, MessagingException, IOException;
	
	User UpdateUser(String currentUsername,String newFirstName, String newLastName, String newUsername, String newEmail,String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistsException, EmailExistsException, MessagingException, IOException;
	
	void deleteUser(long id);
	
	void resetPassword(String email) throws EmailNotFoundException, MessagingException;
	
	User updateProfileImage(String username ,  MultipartFile profileImage) throws UserNotFoundException, UsernameExistsException, EmailExistsException, MessagingException, IOException;

}
