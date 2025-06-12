package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.AccessDeniedException;
import com.example.demo.model.dto.Cart.ShoppingCartDto;
import com.example.demo.model.dto.users.UserCert;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.ShoppingCartService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:8002" }, allowCredentials = "true")
public class ShoppingCartController {

	private ShoppingCartService shoppingCartService;
	
  // 輔助方法：檢查使用者是否登入，並回傳 UserCert
  private UserCert getAuthenticatedUser(HttpSession session) {
      UserCert userCert = (UserCert) session.getAttribute("userCert");
      if (userCert == null) {
          throw new AccessDeniedException("請先登入才能使用購物車功能");
      }
      return userCert;
  }

	@GetMapping
  public ResponseEntity<ApiResponse<ShoppingCartDto>> getMyCart(HttpSession session){
		 UserCert userCert = getAuthenticatedUser(session);
		ShoppingCartDto cart = shoppingCartService.getCart(userCert.getUserId());
		return ResponseEntity.ok(ApiResponse.success("查詢購物車成功", cart));

  }
  
	
}
