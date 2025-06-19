package com.example.demo.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


@RestController
@RequestMapping
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8002"}, allowCredentials = "true")
public class UserRegisterController {
	
	@Value("${app.frontend-url}")
  private String frontendUrl;
	
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
	public ResponseEntity<ApiResponse<Void>> verifyUserEmail(@RequestParam("token") String token ){
		 
		HttpHeaders headers = new HttpHeaders(); 
		try {
			
			userRegisterService.EmailToken(token);
			
			headers.setLocation(URI.create(frontendUrl + "/auth/verify-result?status=success"));
			 
			return new ResponseEntity<>(headers, HttpStatus.FOUND);
					 
		} catch (TokenInvalidException e) {
			String errorMessage = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
			headers.setLocation(URI.create(frontendUrl + "/auth/verify-result?status=error&message=" + errorMessage));
			return new ResponseEntity<>(headers, HttpStatus.FOUND);
		}	catch (RuntimeException e) {
			String errorMessage = URLEncoder.encode("伺服器內部錯誤，請稍後再試", StandardCharsets.UTF_8);
			headers.setLocation(URI.create(frontendUrl + "/auth/verify-result?status=error&message=" + errorMessage));
			return new ResponseEntity<>(headers, HttpStatus.FOUND);
		}
		
	}
	
	//重新發送驗證郵件的端點
	@PostMapping("/resend-verification-email")
	public ResponseEntity<ApiResponse<Void>> resendVerifyEmail(@RequestBody Map<String, String> payload){
		
		String email = payload.get("email");
		// 簡單的驗證
		if(email == null || email.trim().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Email 地址不能為空"));
		}
		
		
		try {
			userRegisterService.resendEmail(email);
			return ResponseEntity.ok(ApiResponse.success("新的驗證郵件已成功發送至 "+email+" 請檢查您的收件匣", null));
		} catch (UserNotFoundException  e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
													 .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
		}	catch (IllegalStateException  e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					 .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
		} catch (RuntimeException  e) {
			System.err.println("重新發送驗證郵件時發生錯誤: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					 .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "重新發送驗證郵件失敗，請稍後再試"));
		}
		
		
		
		
	}
	
	
	
	
}
