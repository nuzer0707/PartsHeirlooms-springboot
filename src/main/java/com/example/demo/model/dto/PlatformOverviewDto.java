package com.example.demo.model.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PlatformOverviewDto {
	
	private Long totalUser;
	private Long activeUser;
	private Long newUsersThisMonth;
	private Long totalProducts;
	private Long productForSale;
	private Long totalTransactions;
	private BigDecimal totalTransactionValue;

}
