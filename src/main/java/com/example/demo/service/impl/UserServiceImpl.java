package com.example.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.enums.UserRole;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.Hash;

public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserMapper userMapper;
	
	@Override
	@Transactional(readOnly = true) // 讀操作，設置 readOnly = true 可以優化
	public List<UserDto> findAllUsers() {
		return userRepository.findAll()
							 .stream()
							 .map(userMapper::toDto)
							 .toList();
	}
	
	@Override
	@Transactional(readOnly = true) // 讀操作，設置 readOnly = true 可以優化
	public UserDto getUser(String username) {
		User user = userRepository.getUser(username);
		if (user==null) {
			return null;
		}
		return userMapper.toDto(user);
	}

	@Override
	@Transactional// 寫操作建議加上事務管理
	public void adduser(String username, String password, String email, Boolean active, UserRole primaryRole) {
		String salt = Hash.getSalt();
		String passwordHash = Hash.getHash(password,salt);
		User user = new User(null,username,passwordHash,salt,email,active,primaryRole);
		userRepository.save(user);
	}



}
