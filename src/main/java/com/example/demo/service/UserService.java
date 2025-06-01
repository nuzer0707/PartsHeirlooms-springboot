package com.example.demo.service;

import java.util.List;

import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.enums.UserRole;

public interface UserService {
	
	
	List<UserDto> findAllUsers();
	public UserDto getUser(String username);
	public void adduser(String username,String password,String email,Boolean active,UserRole primaryRole);
	
	
}
