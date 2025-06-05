package com.example.demo.model.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDto {

	@NotBlank(message = "使用者名稱不能為空")
	@Size(min = 3, max = 50, message = "使用者名稱長度必須在 3 到 50 個字元之間")
	private String username;
	
	@NotBlank(message = "密碼不能為空")
	@Size(min = 4,max = 20,message = "密碼長度必須在 4 到 20 個字元之間")
	private String password;
	
	@NotBlank(message = "信箱不能為空")
	@Email(message = "Email格式不正確")
	private String email;
	
}
