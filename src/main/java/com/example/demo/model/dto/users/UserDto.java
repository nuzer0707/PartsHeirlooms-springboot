package com.example.demo.model.dto.users;

import java.util.List;

import com.example.demo.model.entity.Favorite;
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
