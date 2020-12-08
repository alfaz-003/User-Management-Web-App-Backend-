package com.UserManagementProject.UserManagement.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.UserManagementProject.UserManagement.constant.SecurityConstant;
import com.UserManagementProject.UserManagement.utility.JWTTokenProvider;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
	
	private JWTTokenProvider jwtTokenProvider;
	
	 public JwtAuthorizationFilter(JWTTokenProvider jwtTokenProvider) {
		// TODO Auto-generated constructor stub
		 this.jwtTokenProvider =jwtTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		if(request.getMethod().equalsIgnoreCase(SecurityConstant.OPTIONS_HTTP_METHODS)) {
			response.setStatus(HttpStatus.OK.value());
		}
		else
		{
			String AuthorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
			if(AuthorizationHeader == null || !AuthorizationHeader.startsWith(SecurityConstant.TOKEN_PREFIX)) {
				filterChain.doFilter(request, response);
				return ;
			}
			
			String token = AuthorizationHeader.substring(SecurityConstant.TOKEN_PREFIX.length());
			String username = jwtTokenProvider.getSubject(token);
			
			if(jwtTokenProvider.isTokonValid(username, token)  && SecurityContextHolder.getContext().getAuthentication()== null) {
				List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
				Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}else {
				SecurityContextHolder.clearContext();
			}
		}
		filterChain.doFilter(request, response);
		
	}

}
