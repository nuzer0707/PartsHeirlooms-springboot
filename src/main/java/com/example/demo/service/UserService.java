package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.enums.UserRole;

import jakarta.validation.constraints.NotBlank;

public interface UserService {
	
	
	List<UserDto> findAllUsers();
	public UserDto getUser(String username);
	public void addUser(String username,String password,String email,Boolean active,UserRole primaryRole) ;
	
	
	
}
