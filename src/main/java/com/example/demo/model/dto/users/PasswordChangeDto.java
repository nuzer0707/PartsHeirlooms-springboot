package com.example.demo.model.dto.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeDto {

	@NotBlank(message = "舊密碼不能為空")
	private String oldPassword;
	
	@NotBlank(message = "新密碼不能為空")
	@Size(min = 4,max = 20,message = "新密碼長度必須在 4 到 20 個字元之間")
	private String newPassword;
	
}
