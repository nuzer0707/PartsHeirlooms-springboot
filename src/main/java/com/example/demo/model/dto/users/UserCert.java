package com.example.demo.model.dto.users;

import com.example.demo.model.entity.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

//使用者憑證
//登入成功之後會得到的憑證資料(只有 Getter)

@AllArgsConstructor
@Getter
@ToString
public class UserCert {

	private Integer userId;
	private String username;
	private UserRole primaryRole; 
	
}
