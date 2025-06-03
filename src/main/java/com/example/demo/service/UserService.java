package com.example.demo.service;


import java.util.List;

import com.example.demo.exception.CertException;
import com.example.demo.model.dto.users.PasswordChangeDto;
import com.example.demo.model.dto.users.UserAddDto;
import com.example.demo.model.dto.users.UserDto;
import com.example.demo.model.dto.users.UserUpdateDto;
import com.example.demo.model.entity.enums.UserRole;



public interface UserService {
	
	
	List<UserDto> findAllUsers();
	
	UserDto getUser(String username) throws CertException;
	
	public void addUser(String username,String password,String email,Boolean active,UserRole primaryRole) ;
	
	//新增給一般使用者 (BUYER, SELLER)
	void changePassword(Integer userId,PasswordChangeDto passwordChangeDto) throws CertException;
	
	//新增給 ADMIN
	List<UserDto> findUsersByRoles(List<UserRole> roles);
	
	UserDto addUserByAdmin(UserAddDto addDto) throws CertException;
	
	UserDto updateUserByAdmin(Integer userId,UserUpdateDto updateDto) throws CertException;
	
	void deleteUserByAdmin(Integer userId) throws CertException;
	
}
