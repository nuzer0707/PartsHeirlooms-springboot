package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.TransactionDto;
import com.example.demo.model.dto.checkout.CheckoutRequestDto;
import com.example.demo.model.dto.users.UserCert;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.TransactionService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/transactions") // 我們將與交易相關的 API 都放在這裡
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:8002" }, allowCredentials = "true")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	/**
	 * 從購物車建立交易 (結帳)。
	 * 
	 * @throws UserNotFoundException
	 */

	@PostMapping("/checkout")
	public ResponseEntity<ApiResponse<List<TransactionDto>>> checkoutFromCart(@Valid @RequestBody CheckoutRequestDto checkoutRequest,HttpSession session)
			throws UserNotFoundException {
		UserCert userCert = (UserCert) session.getAttribute("userCert");
		List<TransactionDto> newTransactions = transactionService.createTransactionsFromCart(userCert.getUserId(),checkoutRequest);

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("訂單建立成功", newTransactions));

	}

}
