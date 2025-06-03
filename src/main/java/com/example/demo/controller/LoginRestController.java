package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.exception.CertException;
import com.example.demo.model.dto.users.UserCert;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.CertService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8002"},allowCredentials = "true")
public class LoginRestController {

	@Autowired
	private CertService certService;
	
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Void>> login(@RequestParam String username,@RequestParam String password,HttpSession session){
    try {
      UserCert cert = certService.getCert(username, password);
      session.setAttribute("userCert", cert);
      return ResponseEntity.ok(ApiResponse.success("登入成功", null));
    } catch (CertException e) {
      return ResponseEntity
              .status(HttpStatus.UNAUTHORIZED)
              .body(ApiResponse.error(401, "登入失敗: " + e.getMessage()));
    }
	}
	
	@GetMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(HttpSession session){
		if(session.getAttribute("userCert")==null) {
			return ResponseEntity
									.status(HttpStatus.UNAUTHORIZED)
									.body(ApiResponse.error(401,"登出失敗:尚未登入"));
		}
		session.invalidate();
		return ResponseEntity.ok(ApiResponse.success("登出成功", null));
	}
	
	@GetMapping("/check-login")
	public ResponseEntity<ApiResponse<UserCert>> checkLogin(HttpSession session){
		UserCert userCert = (UserCert) session.getAttribute("userCert");
		 if (userCert != null) {
       return ResponseEntity.ok(ApiResponse.success("登入成功", userCert));
   } else {
     // 如果未登入，data 部分理論上應該是 null，所以 ApiResponse<UserCert> 仍然適用，
     // 或者你可以返回 ApiResponse<Boolean> 並保持原來的邏輯，然後由前端決定是否調用方案二的端點。
     // 為了直接回答問題，這裡假設 check-login 也能返回 UserCert。
  	 return ResponseEntity.ok(ApiResponse.success("用戶未登入", null));
     // 或者返回 401 錯誤
     // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(401, "用戶未登入"));
   }
		
	}
	
	
	
}
