package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.AccessDeniedException;
import com.example.demo.model.dto.Cart.CartItemAddDto;
import com.example.demo.model.dto.Cart.CartItemUpdateDto;
import com.example.demo.model.dto.Cart.ShoppingCartDto;
import com.example.demo.model.dto.users.UserCert;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.ShoppingCartService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:8002" }, allowCredentials = "true")
public class ShoppingCartController {

	private ShoppingCartService shoppingCartService;

	@GetMapping
	public ResponseEntity<ApiResponse<ShoppingCartDto>> getMyCart(HttpSession session) {
		UserCert userCert = (UserCert) session.getAttribute("userCert");
		ShoppingCartDto cart = shoppingCartService.getCart(userCert.getUserId());
		return ResponseEntity.ok(ApiResponse.success("查詢購物車成功", cart));

	}

	@PostMapping("/items")
	public ResponseEntity<ApiResponse<ShoppingCartDto>> addItemToCart(@Valid @RequestBody CartItemAddDto itemDto,
			HttpSession session) {
		UserCert userCert = (UserCert) session.getAttribute("userCert");
		ShoppingCartDto updateCart = shoppingCartService.addItemToCart(userCert.getUserId(), itemDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("商品已加入購物車", updateCart));
	}

	@PutMapping("/items/{productId}")
	public ResponseEntity<ApiResponse<ShoppingCartDto>> updateItemQuantity(@PathVariable Integer productId,
			@Valid @RequestBody CartItemUpdateDto updateDto, HttpSession session) {
		UserCert userCert = (UserCert) session.getAttribute("userCert");
		ShoppingCartDto updatedCart = shoppingCartService.updateItemQuantity(userCert.getUserId(), productId,
				updateDto.getQuantity());
		return ResponseEntity.ok(ApiResponse.success("購物車商品數量已更新", updatedCart));
	}

	@DeleteMapping("/items/{productId}")
	public ResponseEntity<ApiResponse<ShoppingCartDto>> removeItemFromCart(@PathVariable Integer productId,
			HttpSession session) {
		UserCert userCert = (UserCert) session.getAttribute("userCert");
		shoppingCartService.clearCart(userCert.getUserId());
		return ResponseEntity.ok(ApiResponse.success("商品已從購物車移除", null));
	}

	@DeleteMapping
	public ResponseEntity<ApiResponse<Void>> clearMyCart(HttpSession session) {
		UserCert userCert = (UserCert) session.getAttribute("userCert");
		shoppingCartService.clearCart(userCert.getUserId());
		return ResponseEntity.ok(ApiResponse.success("購物車已清空", null));
	}

}
