package com.UserManagementProject.UserManagement.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="users")

public class User {
	
	@Id
	
	@GeneratedValue(strategy=GenerationType.AUTO)
	
	@Column(nullable=false,updatable=false)
	
	private Long id; 
	
	private String userId ;
	
	private String firstName ;
	
	private String lastName ;
	
	private String username ;
	
	private String password ;
	
	private String email ;
	
	private String profileImgUrl ;
	
	private Date lastLogindate ;
	
	private Date lastLogindateDisplay ;
	
	private Date joinDate ;
	
	private String role ; //ROLE_ADMIN, ROLE_USER
	
	private String[] authorities ; // ADMIN(Edit,Update,Delete) , USER(Edit,View) 
	
	private boolean isActive ;
	
	private boolean isNotLocked ;
	
	
//default cons
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	
//cons with par
	
	
	public User(Long id, String userId, String firstName, String lastName, String username, String password,
			String email, String profileImgUrl, Date lastLogindate, Date lastLogindateDisplay, Date joinDate,
			String role, String[] authorities, boolean isActive, boolean isNotLocked) {
		super();
		this.id = id;
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.password = password;
		this.email = email;
		this.profileImgUrl = profileImgUrl;
		this.lastLogindate = lastLogindate;
		this.lastLogindateDisplay = lastLogindateDisplay;
		this.joinDate = joinDate;
		this.role = role;
		this.authorities = authorities;
		this.isActive = isActive;
		this.isNotLocked = isNotLocked;
	}

	
//getters & setters
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getProfileImgUrl() {
		return profileImgUrl;
	}

	public void setProfileImgUrl(String profileImgUrl) {
		this.profileImgUrl = profileImgUrl;
	}

	public Date getLastLogindate() {
		return lastLogindate;
	}

	public void setLastLogindate(Date lastLogindate) {
		this.lastLogindate = lastLogindate;
	}

	public Date getLastLogindateDisplay() {
		return lastLogindateDisplay;
	}

	public void setLastLogindateDisplay(Date lastLogindateDisplay) {
		this.lastLogindateDisplay = lastLogindateDisplay;
	}

	public Date getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String[] getAuthorities() {
		return authorities;
	}

	public void setAuthorities(String[] authorities) {
		this.authorities = authorities;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isNotLocked() {
		return isNotLocked;
	}

	public void setNotLocked(boolean isNotLocked) {
		this.isNotLocked = isNotLocked;
	}
	
	
	

}
