package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.TokenInvalidException;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.users.UserRegisterDto;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.UserRegisterService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8002"}, allowCredentials = "true")
public class UserRegisterController {
	
	@Autowired
	private UserRegisterService userRegisterService;
	
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<Void>>registerUser(@Valid @RequestBody UserRegisterDto registerDto){
		try {
			userRegisterService.registerNewUser(registerDto);
			String successMessage = String.format( "註冊請求已提交。一封包含驗證連結的郵件已發送至 %s，請檢查您的收件匣並點擊連結以激活您的帳戶", registerDto.getEmail());
			return ResponseEntity.status(HttpStatus.ACCEPTED)
													 .body(ApiResponse.success(successMessage, null));
		} catch (UserAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
													 .body(ApiResponse.error(HttpStatus.CONFLICT.value(), e.getMessage()));
		}catch (RuntimeException e) {
			System.err.println("註冊過程中發生錯誤"+ e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
												   .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),"註冊失敗，伺服器內部錯誤"+e.getMessage()));
		}		
	}
	
	
	@GetMapping("/verify-email")
	public ResponseEntity<ApiResponse<Void>> verifyUserEmail(@RequestParam("token") String token){
		
		try {
			userRegisterService.EmailToken(token);
			return ResponseEntity.ok(ApiResponse.success("電子郵件驗證成功！您的帳戶已激活，現在可以登入了。", null));
					 
		} catch (TokenInvalidException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
													 .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
		}	catch (RuntimeException e) {
			System.err.println("驗證郵件過程中發生錯誤: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					 .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),"驗證失敗，伺服器內部錯誤"));
		}
		
	}
	
	
}
