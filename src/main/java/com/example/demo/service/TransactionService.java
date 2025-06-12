package com.example.demo.service;

import java.util.List;

import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.TransactionDto;
import com.example.demo.model.dto.checkout.CheckoutRequestDto;
import com.example.demo.model.entity.enums.TransactionStatus;
import com.example.demo.model.entity.enums.UserRole;

public interface TransactionService {

	TransactionDto addTransaction(Integer buyerId,Integer productId,Integer chosenTransactionDetailId) throws UserNotFoundException;
	
	TransactionDto getTransactionById(Integer transactionId,Integer requestingUserId ,UserRole userRole);
	
	List<TransactionDto> getTransactionsByBuyer(Integer buyserId);
	
	List<TransactionDto> getTransactionsBySeller(Integer sellerId);
	
	List<TransactionDto> getAllTransactionsForAdmin();
	
	TransactionDto updateTransactionsStatus(Integer transactionId ,TransactionStatus  newStatus, Integer requestingUserId,UserRole userRole);

	List<TransactionDto> getTransactionsByBuyerAndStatuses(Integer buyerId, List<TransactionStatus> statuses) throws UserNotFoundException;
	
	List<TransactionDto> getTransactionsBySellerAndStatuses(Integer sellerId, List<TransactionStatus> statuses) throws UserNotFoundException;

	TransactionDto buyerCancelTransaction(Integer transactionId, Integer buyerUserId) throws UserNotFoundException; // 新增買家取消

	
	

	List<TransactionDto> createTransactionsFromCart(Integer userId, CheckoutRequestDto checkoutRequest) throws UserNotFoundException;
	
	
}
