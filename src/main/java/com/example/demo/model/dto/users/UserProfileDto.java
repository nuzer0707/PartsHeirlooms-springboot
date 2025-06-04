package com.example.demo.model.dto.users;

import com.example.demo.model.entity.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {

	private Integer userId;
	private String username;
	private String email;
	private UserRole primaryRole;
	
}
