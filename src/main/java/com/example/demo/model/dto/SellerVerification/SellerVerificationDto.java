package com.example.demo.model.dto.SellerVerification;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.model.entity.User;
import com.example.demo.model.entity.enums.VerificationStatus;

import lombok.Data;

@Data
public class SellerVerificationDto {

	private Integer verificationId;
	private Integer userId;
	private String username;
	private VerificationStatus status;
	private String adminRemarks;
	private LocalDateTime reviewedAt;
	private LocalDateTime submittedAt;
	//private User reviewedByAdmin;
	private Integer reviewedByAdminId; 
	 private String reviewedByAdminUsername;
	
	private List<String> verificationImageBases64;
	
}
