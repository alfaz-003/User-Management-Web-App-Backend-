package com.UserManagementProject.UserManagement.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.UserManagementProject.UserManagement.constant.FileConstant;
import com.UserManagementProject.UserManagement.constant.SecurityConstant;
import com.UserManagementProject.UserManagement.domain.HttpResponse;
import com.UserManagementProject.UserManagement.domain.User;
import com.UserManagementProject.UserManagement.domain.UserPrincipal;
import com.UserManagementProject.UserManagement.exception.domain.EmailExistsException;
import com.UserManagementProject.UserManagement.exception.domain.EmailNotFoundException;
import com.UserManagementProject.UserManagement.exception.domain.ExceptionHandling;
import com.UserManagementProject.UserManagement.exception.domain.UserNotFoundException;
import com.UserManagementProject.UserManagement.exception.domain.UsernameExistsException;
import com.UserManagementProject.UserManagement.service.UserService;
import com.UserManagementProject.UserManagement.utility.JWTTokenProvider;


@RestController
@RequestMapping(path = {"/" , "/user"})
public class UserResource extends ExceptionHandling {
	
	private static final String EMAIL_WITH_NEW_PASSWORD_IS_SENT_TO = "Email with new password is sent to: ";
	private static final String USER_DELETED_SUCCESSFULLY = "User Deleted Successfully";
	
	private UserService userService;
	private AuthenticationManager authenticationManager;
	private JWTTokenProvider jwtTokenProvider ;
	
	@Autowired
	public UserResource(UserService userService, AuthenticationManager authenticationManager,
			JWTTokenProvider jwtTokenProvider) {
		super();
		this.userService = userService;
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
	}



	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody User user)  {
	authenticate(user.getUsername(), user.getPassword());
	User loginUser = userService.findUserByUsername(user.getUsername());
	UserPrincipal userPrincipal = new UserPrincipal(loginUser);
	HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
		return new ResponseEntity<>(loginUser, jwtHeader, HttpStatus.OK);
	}

	

	@PostMapping("/register")
	public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, UsernameExistsException, EmailExistsException, MessagingException {
		User newUser = userService.register(user.getFirstName(),user.getLastName(), user.getUsername(), user.getEmail());
		return new ResponseEntity<>(newUser,HttpStatus.OK);
	}
	
	
	@PostMapping("/add")
	public ResponseEntity<User> addNewuser( @RequestParam("firstName") String firstName,
											@RequestParam("lastName") String lastName,
											@RequestParam("username") String username,
											@RequestParam("email") String email,
											@RequestParam("role") String role,
											@RequestParam("isActive") String isActive,
											@RequestParam("isNotLocked") String isNotLocked,
											@RequestParam(value = "profileImage",required= false) MultipartFile profileImage) throws UserNotFoundException, UsernameExistsException, EmailExistsException, MessagingException, IOException {
		
		
		User newUser = userService.addNewUser(firstName, lastName, username, email, role,Boolean.parseBoolean(isNotLocked) , Boolean.parseBoolean(isActive), profileImage);
		return new ResponseEntity<>(newUser,HttpStatus.OK);
			
	}
	
	
	
	@PostMapping("/update")
	public ResponseEntity<User> update( @RequestParam("currentUsername") String currentUsername,
											@RequestParam("firstName") String firstName,
											@RequestParam("lastName") String lastName,
											@RequestParam("username") String username,
											@RequestParam("email") String email,
											@RequestParam("role") String role,
											@RequestParam("isActive") String isActive,
											@RequestParam("isNotLocked") String isNotLocked,
											@RequestParam(value = "profileImage",required= false) MultipartFile profileImage) throws UserNotFoundException, UsernameExistsException, EmailExistsException, MessagingException, IOException {
		
		
		User updatedUser = userService.UpdateUser(currentUsername,firstName, lastName, username, email, role,Boolean.parseBoolean(isNotLocked) , Boolean.parseBoolean(isActive), profileImage);
		return new ResponseEntity<>(updatedUser,HttpStatus.OK);
			
	}
	
	
	@GetMapping("/find/{username}")
	public ResponseEntity<User> getUser(@PathVariable("username") String username) {
		User user = userService.findUserByUsername(username);
		return new ResponseEntity<>(user,HttpStatus.OK);	
	}
	
	
	
	@GetMapping("/list")
	public ResponseEntity< java.util.List<User> > getAllUsers() {
		java.util.List<User> users = userService.getUsers();
		return new ResponseEntity<>(users,HttpStatus.OK);	
	}
	
	
	@GetMapping("/resetpassword/{email}")
	public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException, MessagingException {
		 userService.resetPassword(email);
		return response(HttpStatus.OK,EMAIL_WITH_NEW_PASSWORD_IS_SENT_TO+email);	
	}
	
	
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id")  long id) {
    	userService.deleteUser(id);
		return response(HttpStatus.NO_CONTENT, USER_DELETED_SUCCESSFULLY);
    }
    
    
    @PostMapping("/updateProfileImage")
	public ResponseEntity<User> updateProfileImage( 
											@RequestParam("username") String username,
											@RequestParam(value = "profileImage") MultipartFile profileImage) throws UserNotFoundException, UsernameExistsException, EmailExistsException, MessagingException, IOException {
		
		
    	User user = userService.updateProfileImage(username, profileImage);
		return new ResponseEntity<>(user,HttpStatus.OK);
			
	}
    
    
    @GetMapping(path ="/image/profile/{username}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
    	URL url = new URL(FileConstant.TEMP_PROFILE_BASE_URL + username);
    	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    	
    	try(InputStream inputStream = url.openStream()) {
    		int bytesRead;
    		byte[] chunk = new byte[1024];
    		while((bytesRead = inputStream.read(chunk)) > 0) {
    			byteArrayOutputStream.write(chunk, 0, bytesRead);
    		}
    	}
    	
		return byteArrayOutputStream.toByteArray();
    }
    
    
    
    @GetMapping(path = "/image/profile/{filename}" , produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName ) throws IOException {
		return Files.readAllBytes(Paths.get(FileConstant.USER_FOLDER + username + FileConstant.FORWARD_SLASH + fileName)); 	
    }
    
    
    
	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
		return new ResponseEntity<>(new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toUpperCase(),message.toUpperCase()),httpStatus);
	}



	private HttpHeaders getJwtHeader(UserPrincipal user) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(SecurityConstant.JWT_TOKEN_HEADER , jwtTokenProvider.generateJwtToken(user));
		return headers;
	}


	private void authenticate(String username, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		
	}

}
 