package com.example.demo.service;

import java.util.List;

import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.SellerVerificationApplyDto;
import com.example.demo.model.dto.SellerVerificationDto;
import com.example.demo.model.dto.SellerVerificationReviewDto;

public interface SellerVerificationService {
	
	SellerVerificationDto applyForSeller(SellerVerificationApplyDto applyDto,Integer applicantUserId)throws UserNotFoundException;
	
	SellerVerificationDto getVerificationByIdForAdmin(Integer verificationId);
	
	SellerVerificationDto getUserVerificationStatus(Integer userId)throws UserNotFoundException;
	
	List<SellerVerificationDto>getAllPendingVerificationsForAdmin();
	
	List<SellerVerificationDto>getAllVerificationsForAdmin();
	
	SellerVerificationDto reviewVerification(Integer verifivationId,SellerVerificationReviewDto reviewDto,Integer adminUserId)throws UserNotFoundException;
	
}
