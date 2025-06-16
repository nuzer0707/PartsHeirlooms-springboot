package com.example.demo.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TransactionShipmentDetailDto {
	
	private String productTitle;
	private Integer quantity;
	private BigDecimal pricePerItem;
	private String methodName;
	private String address;
	private LocalDateTime meetupTime;
	private String notes;
	private BigDecimal meetupLatitude;
	private BigDecimal meetupLongitude;

}
