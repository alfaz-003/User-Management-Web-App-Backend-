package com.UserManagementProject.UserManagement.service.impl;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.UserManagementProject.UserManagement.constant.FileConstant;
import com.UserManagementProject.UserManagement.domain.User;
import com.UserManagementProject.UserManagement.domain.UserPrincipal;
import com.UserManagementProject.UserManagement.enumeration.Role;
import com.UserManagementProject.UserManagement.exception.domain.EmailExistsException;
import com.UserManagementProject.UserManagement.exception.domain.EmailNotFoundException;
import com.UserManagementProject.UserManagement.exception.domain.UserNotFoundException;
import com.UserManagementProject.UserManagement.exception.domain.UsernameExistsException;
import com.UserManagementProject.UserManagement.repository.UserRepository;
import com.UserManagementProject.UserManagement.service.EmailService;
import com.UserManagementProject.UserManagement.service.LoginAttemptService;
import com.UserManagementProject.UserManagement.service.UserService;


@Service
@Transactional
@Qualifier("UserDetailsService")

public class UserServiceImpl implements UserService , UserDetailsService {
	
	private static final String USER_NOT_FOUND_BY_USERNAME = "User not found by username: ";
	public static final String NO_USER_FOUND_BY_USERNAME = "No user found by Username ";
	private static final String EMAIL_ALREADY_EXISTS = "Email already exists";
	private static final String USERNAME_ALREADY_EXISTS = "Username already exists";
	
	private Logger LOGGER = LoggerFactory.getLogger(getClass()); 
	private UserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;
	private LoginAttemptService loginAttemptService;
	private EmailService emailService;
	private CopyOption REPLACE_EXISTING;
	
	@Autowired
	 public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder ,
			 LoginAttemptService loginAttemptService , EmailService emailService) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder ;
		this.loginAttemptService = loginAttemptService;
		this.emailService = emailService ;
		
	}

	

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findUserByUsername(username); 
		
		if(user == null) {
			LOGGER.error(USER_NOT_FOUND_BY_USERNAME + username);
			throw new UsernameNotFoundException(USER_NOT_FOUND_BY_USERNAME + username);
		}
		else
		{
			validateLoginAttempt(user);
			user.setLastLogindateDisplay(user.getLastLogindate());
			user.setLastLogindate(new Date());
			userRepository.save(user);
			
			UserPrincipal userPrincipal = new UserPrincipal(user);
			LOGGER.info("Returning found user by Username "+username);
			return userPrincipal;
		}
		
	}

	private void validateLoginAttempt(User user) {
		
		if(user.isNotLocked()) {
			if(loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
				user.setNotLocked(false);
			}
			else {
				user.setNotLocked(true);
			}
		}
		else {
			loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
		}
	
		
	}



	@Override
	public User register(String firstName, String lastName, String username, String email) throws 
	UserNotFoundException, UsernameExistsException, EmailExistsException, MessagingException {
		validateNewUsernameAndEmail(StringUtils.EMPTY, username, email) ;
		User user =new User();
		user.getId();
		user.setUserId(generateUserId());
		String password = generatePassword();
		String encodedPassword = encodePassword(password);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUsername(username);
		user.setEmail(email);
		user.setJoinDate(new Date());
		user.setPassword(encodedPassword);
		user.setActive(true);
		user.setNotLocked(true);
		user.setRole(Role.ROLE_USER.name());
		user.setAuthorities(Role.ROLE_USER.getAuthorities());
		user.setProfileImgUrl(getTemporaryprofileImageUrl(username));
		userRepository.save(user);
		LOGGER.info("New user password "+password);
		emailService.sendNewpasswordEmail(firstName, password, email);
		
		
		return user;
	}
	
	
	
	@Override
	public User addNewUser(String firstName, String lastName, String username, String email, String role,
			boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws 
	UserNotFoundException, UsernameExistsException, EmailExistsException, MessagingException, IOException {
		
		validateNewUsernameAndEmail(StringUtils.EMPTY, username, email) ;
		User user =new User();
		String password = generatePassword();
		String encodedPassword = encodePassword(password);
		user.setUserId(generateUserId());
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setJoinDate(new Date());
		user.setUsername(username);
		user.setEmail(email);
		user.setActive(isActive);
		user.setPassword(encodedPassword);
		user.setNotLocked(isNotLocked);
		user.setRole(getRoleEnumName(role).name());
		user.setAuthorities(getRoleEnumName(role).getAuthorities());
		user.setProfileImgUrl(getTemporaryprofileImageUrl(username));
		userRepository.save(user);
		saveProfileImage(user, profileImage);
		return user;
	}
	



	@Override
	public User UpdateUser(String currentUsername, String newFirstName, String newLastName, String newUsername,
			String newEmail, String role, boolean isNotLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistsException, EmailExistsException, MessagingException, IOException {
		
		User currentuser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail) ;
		currentuser.setFirstName(newFirstName);
		currentuser.setLastName(newLastName);
		currentuser.setJoinDate(new Date());
		currentuser.setUsername(newUsername);
		currentuser.setEmail(newEmail);
		currentuser.setActive(isActive);
		currentuser.setNotLocked(isNotLocked);
		currentuser.setRole(getRoleEnumName(role).name());
		currentuser.setAuthorities(getRoleEnumName(role).getAuthorities());
		userRepository.save(currentuser);
		saveProfileImage(currentuser, profileImage);
		return currentuser;
	}



	@Override
	public void deleteUser(long id) {
		userRepository.deleteById(id);
	}
	
	@Override
	public void resetPassword(String email) throws EmailNotFoundException, MessagingException {
		User user = userRepository.findUserByEmail(email);
		if(user == null) {
			throw new EmailNotFoundException("No user found by this Email" + email);
		}
		String password = generatePassword();
		user.setPassword(encodePassword(password));
		userRepository.save(user);
		emailService.sendNewpasswordEmail(user.getFirstName(), password, email);
		
	}




	@Override
	public User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistsException, EmailExistsException, MessagingException, IOException {
		User user = validateNewUsernameAndEmail(username, null, null);
		saveProfileImage(user, profileImage);
		return user;
	}
	
	
	

	
	
	@Override
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@Override
	public User findUserByUsername(String username) {
		return userRepository.findUserByUsername(username);
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}

	private String getTemporaryprofileImageUrl(String username) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/image/profile/"+username).toUriString();
	}

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
		
	}

	private String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(10);
	}

	private String generateUserId() {
		return RandomStringUtils.randomNumeric(10);
	}
	

	private void saveProfileImage(User user , MultipartFile profileImage) throws IOException {
		if(profileImage != null) {
			Path userFolder = Paths.get(FileConstant.USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
			if(!Files.exists(userFolder)) {
				Files.createDirectories(userFolder);
				LOGGER.info(FileConstant.DIRECTORY_CREATED + userFolder);
			}
			Files.deleteIfExists(Paths.get(userFolder + user.getUsername()+ FileConstant.DOT + FileConstant.JPG_EXTENTION));
			Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + FileConstant.DOT + FileConstant.JPG_EXTENTION), REPLACE_EXISTING);
			user.setProfileImgUrl(setProfileImageurl(user.getUsername()));
			userRepository.save(user);
			LOGGER.info(FileConstant.FILE_SAVED_UN_FILE_SYSTEM + profileImage.getOriginalFilename());
		}
		 
	 }
	

	private String setProfileImageurl(String username) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/image/profile/" + username 
				+ FileConstant.FORWARD_SLASH + username + FileConstant.DOT + FileConstant.JPG_EXTENTION).toUriString() ;
	}





	private Role getRoleEnumName(String role) {
		return Role.valueOf(role.toUpperCase());
	}

	
	

	private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws
	UserNotFoundException, UsernameExistsException, EmailExistsException
	{

		User userByNewUsername = findUserByUsername(newUsername);
		User userByNewEmail = findUserByEmail(newEmail);
		
		if(StringUtils.isNotBlank(currentUsername)) {
			User currentUser = findUserByUsername(currentUsername);
			
			if(currentUser == null) {
				throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME+currentUsername);
			}
			if(userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
				throw new UsernameExistsException(USERNAME_ALREADY_EXISTS);
			}
			if(userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
				throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
			}
			
			return currentUser;
			
		} else {
			
			if(userByNewUsername != null) {
				throw new UsernameExistsException(USERNAME_ALREADY_EXISTS);
			}
		
			if(userByNewEmail != null) {
				throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
			}
			
			return null ;
		}
		
	}



	



	
	
}
	
	
