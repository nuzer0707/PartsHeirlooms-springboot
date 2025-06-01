package com.example.demo.service;

import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.enums.UserRole;

public interface UserService {

	public UserDto getUser(String username);
	public void adduser(String username,String password,String email,Boolean active,UserRole primaryRole);
	
	
}
