package com.example.demo.model.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SalesReportDto {

	private BigDecimal totalRevenue;
	private Long totalOrders;
	private Long totalItemsSold;
	
}
