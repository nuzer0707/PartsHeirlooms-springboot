package com.example.demo.model.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TransactionShipmentDetailDto {
	 	private String methodName;
	    private String address;
	    private LocalDateTime meetupTime;
	    private String notes;

}
