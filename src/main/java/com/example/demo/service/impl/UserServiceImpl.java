package com.example.demo.service.impl;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.CertException;
import com.example.demo.exception.PasswordInvalidException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.dto.users.UserPasswordChangeDto;
import com.example.demo.model.dto.users.UserProfileDto;
import com.example.demo.model.dto.users.UserAddDto;
import com.example.demo.model.dto.users.UserDto;
import com.example.demo.model.dto.users.UserUpdateDto;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.enums.UserRole;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.Hash;


@Service
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
	public UserDto getUser(String username) throws UserNotFoundException{
		User user = userRepository
							.findByUsername(username)
							.orElseThrow(()-> new UserNotFoundException("找不到使用者："+username));

		return userMapper.toDto(user);
	}

	@Override
	@Transactional// 寫操作建議加上事務管理
	public void addUser(String username, String password, String email, Boolean active, UserRole primaryRole) {
		String salt = Hash.getSalt();
		String passwordHash = Hash.getHash(password,salt);
		
		User user = User.builder()
					.username(username)
					.passwordHash(passwordHash)
					.hashSalt(salt)
					.email(email)
					.active(active)
					.primaryRole(primaryRole)
					.build();		
		userRepository.save(user);
	}
	
	
	//*************新增給一般使用者 (BUYER, SELLER)*************
	
	@Override
	@Transactional(readOnly = true)
	public UserProfileDto getUserProfile(Integer userId) throws CertException {
		User user = userRepository.findById(userId)
															.orElseThrow(()->new UserNotFoundException("找不到使用者 ID：" + userId));
		return new UserProfileDto(user.getUserId(),user.getUsername(),user.getEmail(),user.getPrimaryRole());
	}
	
	@Override
	@Transactional// 寫操作建議加上事務管理 
	public void changePassword(Integer userId, UserPasswordChangeDto passwordChangeDto) throws CertException {
		User user = userRepository
							.findById(userId)
							.orElseThrow(()-> new UserNotFoundException("找不到使用者 ID：" + userId));
		
		 
		// 驗證舊密碼
		String oldPasswordHash = Hash.getHash(passwordChangeDto.getOldPassword(),user.getHashSalt());
		
		if(!oldPasswordHash.equals(user.getPasswordHash())) {
			throw new PasswordInvalidException("舊密碼不正確");
		}
		
		// 更新為新密碼
		String newSalt = Hash.getSalt();// 更改密碼時，最好也重新生成鹽
		String newPasswordHash = Hash.getHash(passwordChangeDto.getNewPassword(),newSalt);
		user.setPasswordHash(newPasswordHash);
		user.setHashSalt(newSalt);
		userRepository.save(user);
		
	}

	
	//*************新增給 ADMIN*************
	
	@Override
	@Transactional(readOnly = true)
	public List<UserDto> findUsersByRoles(List<UserRole> roles) {
		
		return userRepository.findByPrimaryRoleIn(roles)
				.stream()
				.map(userMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public UserDto addUserByAdmin(UserAddDto addDto) throws CertException {

		if(userRepository.existsByUsername(addDto.getUsername())) {
			throw new UserNotFoundException("新增失敗：使用者名稱"+addDto.getUsername()+"已存在");
		}
		
		if(userRepository.existsByEmail(addDto.getEmail())) {
			throw new UserNotFoundException("新增失敗：Email"+addDto.getEmail()+"已被使用");
		}
		
		String salt = Hash.getSalt();
		String passwordHash = Hash.getHash(addDto.getPassword(),salt);
		
		User user = new User();
		user.setUsername(addDto.getUsername());
		user.setPasswordHash(passwordHash);
		user.setHashSalt(salt);
		user.setEmail(addDto.getEmail());
		user.setActive(addDto.getActive() != null ? true:false);
		user.setPrimaryRole(addDto.getPrimaryRole());
		
		User savedUser = userRepository.save(user);
		return userMapper.toDto(savedUser);
		
	}

	@Override
	@Transactional
	public UserDto updateUserByAdmin(Integer userId, UserUpdateDto updateDto) throws CertException {
		
		User user = userRepository.findById(userId)
															.orElseThrow(()->new UserNotFoundException("更新失敗:找不到使用者 ID:"+ userId));
		if(updateDto.getEmail() != null && !updateDto.getEmail().equalsIgnoreCase(user.getEmail())) {
						if(userRepository.existsByEmail(updateDto.getEmail())) {
							throw new UserNotFoundException("新增失敗：Email"+updateDto.getEmail()+"已被使用");
						}
						user.setEmail(updateDto.getEmail());
		}
	  // 更新密碼
		if(updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
			
			String newSalt = Hash.getSalt();
			String newPasswordHash = Hash.getHash(updateDto.getPassword(),newSalt);
 			
			user.setPasswordHash(newPasswordHash);
			user.setHashSalt(newSalt);
		}
		
		// 更新 active 狀態
		if(updateDto.getActive() != null) {
			user.setActive(updateDto.getActive());
		}
		
		//更新身分
		if(updateDto.getPrimaryRole()!=null) {
			user.setPrimaryRole(updateDto.getPrimaryRole());
		}
		
		User savedUser = userRepository.save(user);
		return userMapper.toDto(savedUser);
	}

	@Override
	@Transactional
	public void deleteUserByAdmin(Integer userId) throws CertException {
		
		if (!userRepository.existsById(userId)) {
       throw new UserNotFoundException("刪除失敗：找不到使用者 ID：" + userId);
		}
		
		userRepository.deleteById(userId);
	}





}
