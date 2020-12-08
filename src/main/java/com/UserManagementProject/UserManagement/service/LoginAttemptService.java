package com.UserManagementProject.UserManagement.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class LoginAttemptService {
	
	private static final int MAXIMUM_NUMBERS_OF_ATTEMPTS = 3;
	private static final int ATTEMPT_INCREMENT=1;
	
	private LoadingCache<String, Integer> loginAttemptcache ;
	
	public LoginAttemptService() {
		super();
		loginAttemptcache = CacheBuilder.newBuilder().expireAfterWrite(15,TimeUnit.MINUTES)
				.maximumSize(100).build(new CacheLoader<String, Integer>() {
					public Integer load(String key) {
						return 0 ;
					}
				});
		
}
	public void evictUserFromLoginAttemptCache(String username) {
		loginAttemptcache.invalidate(username);
	}
	
	public void addUserToLoginAttemptCache(String username) {
		int attempts = 0;
		try {
			attempts = ATTEMPT_INCREMENT + loginAttemptcache.get(username);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loginAttemptcache.put(username, attempts);
		
	}


public boolean hasExceededMaxAttempts(String username) {
	try {
		return loginAttemptcache.get(username) >= MAXIMUM_NUMBERS_OF_ATTEMPTS ;
	} catch (ExecutionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return false;
}

}
