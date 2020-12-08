package com.UserManagementProject.UserManagement.exception.domain;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.UserManagementProject.UserManagement.domain.HttpResponse;

@RestControllerAdvice
public class ExceptionHandling {
	
	public static final String ACCOUNT_LOCKED = "Your account has been locked.Please contact the administrator";
	public static final String METHOD_IS_NOT_ALLOWED = "the request method is not Allowed";
	public static final String INTERNAL_SERVER_ERROR_MSG = "There is an issue with Internal Server";
	public static final String INCORRECT_CREDENTIALS = "Username or password is incorrect" ;
	public static final String  ACCOUNT_DISABLED = "Your account is not Accessible.Plz contact Admin. ";
	public static final String ERROR_PROCCESSING_FILE = "Unable to process the file ";
	public static final String NOT_ENOUGH_PERMISSION = "You do not have required permission to Access ";
	
	
	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<HttpResponse> accountDisabledException() {
		return createHttpResponse(HttpStatus.BAD_REQUEST, ACCOUNT_DISABLED);
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<HttpResponse> badCredentialsException() {
		return createHttpResponse(HttpStatus.BAD_REQUEST, INCORRECT_CREDENTIALS);
	}
	
	@ExceptionHandler(LockedException.class)
	public ResponseEntity<HttpResponse> lockedException() {
		return createHttpResponse(HttpStatus.BAD_REQUEST,ACCOUNT_LOCKED );
	}
	

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<HttpResponse> accessDeniedException() {
		return createHttpResponse(HttpStatus.FORBIDDEN,NOT_ENOUGH_PERMISSION );
	}
	
	@ExceptionHandler(EmailExistsException.class)
	public ResponseEntity<HttpResponse> emailExistsException(EmailExistsException exception) {
		return createHttpResponse(HttpStatus.UNAUTHORIZED,exception.getMessage());
	}
	
	
	private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus,String message){
		
		
		return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, 
				httpStatus.getReasonPhrase().toUpperCase(),
				message.toUpperCase()),httpStatus);
		
		
		
	}

}
