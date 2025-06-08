package com.example.demo.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.enums.TransactionStatus;

import lombok.Data;

@Data
public class TransactionDto {

	private Integer transactionId;
	
	// 產品資訊
	private Integer productId;
	private String productTitle;
	private String productFirstImageBases64;
	
	// 賣家和買家資訊
	private Integer sellerUserId;
	private String sellerUsername;
	private Integer buyerUserId;
	private String buyerUsername;
	
	// 交易資訊
	private BigDecimal finalPrice;
	private TransactionStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
}
