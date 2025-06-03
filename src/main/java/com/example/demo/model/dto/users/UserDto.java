package com.example.demo.model.dto.users;

import com.example.demo.model.entity.enums.UserRole;

import lombok.Data;

@Data
public class UserDto {
	private Integer userId;
	private String username;
	private String email;
	private Boolean active;
	private UserRole primaryRole;
}
