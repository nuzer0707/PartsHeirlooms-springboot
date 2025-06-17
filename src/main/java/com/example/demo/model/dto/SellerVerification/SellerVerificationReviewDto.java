package com.example.demo.model.dto.SellerVerification;

import com.example.demo.model.entity.enums.VerificationStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SellerVerificationReviewDto {
	
	@NotNull(message = "審核狀態不能為空")
	private VerificationStatus status;
	private String adminRemarks;
	
	
	
}
