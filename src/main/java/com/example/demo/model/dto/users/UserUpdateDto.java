package com.example.demo.model.dto.users;

import com.example.demo.model.entity.enums.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDto {

	/* 通常不建議修改 username，但如果業務允許
	 * @Size(min = 3,max = 20,message = "使用者名稱長度必須在 3 到 20 個字元之間") private String
	 * username;
	 */
	
		@Size(min = 4,max = 20,message = "密碼長度必須在 4 到 20 個字元之間")
		private String password;
	
	  @Email(message = "Email格式不正確")
		private String email;
	  
	  private Boolean active;
	  
	  private UserRole primaryRole;
	
}
