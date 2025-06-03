package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.CertException;
import com.example.demo.exception.PasswordInvalidException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.users.PasswordChangeDto;
import com.example.demo.model.dto.users.UserCert;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = {"/profile"})
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8002"},allowCredentials = "true")
public class ProfileController {

	@Autowired
	private UserService userService;
	
	@PutMapping("/password")
	private ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto,HttpSession session){
		
		UserCert userCert = (UserCert) session.getAttribute("userCert");
		
		if(userCert == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(401,"請先登入"));
		}
		try {
			userService.changePassword(userCert.getUserId(), passwordChangeDto);
			return ResponseEntity.ok(ApiResponse.success("密碼修改成功", null));
			
		} catch (CertException e) {
			 // 根據例外類型返回不同狀態碼
			if(e instanceof PasswordInvalidException) {
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(400,"密碼修改失敗"+e.getMessage()));
				}else if(e instanceof UserNotFoundException){
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(404,"密碼修改失敗"+e.getMessage()));
				}
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(500,"密碼修改失敗"+e.getMessage()));
		}
		
		
	}
	
}
