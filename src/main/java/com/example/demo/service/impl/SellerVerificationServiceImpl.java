package com.example.demo.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.ProductOperationException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.mapper.SellerVerificationMapper;
import com.example.demo.model.dto.SellerVerificationApplyDto;
import com.example.demo.model.dto.SellerVerificationDto;
import com.example.demo.model.dto.SellerVerificationReviewDto;
import com.example.demo.model.entity.SellerVerification;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.enums.UserRole;
import com.example.demo.model.entity.enums.VerificationStatus;
import com.example.demo.repository.SellerVerificationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.SellerVerificationService;


@Service
public class SellerVerificationServiceImpl implements SellerVerificationService{

	@Autowired
	private SellerVerificationRepository verificationRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SellerVerificationMapper verificationMapper;
	
	@Override
	@Transactional
	public SellerVerificationDto applyForSeller(SellerVerificationApplyDto applyDto, Integer applicantUserId)
			throws UserNotFoundException {
		User applicant = userRepository.findById(applicantUserId)
					.orElseThrow(()->new UserNotFoundException("申請者用戶不存在 ID:"+applicantUserId));
		if(UserRole.SELLER.equals(applicant.getPrimaryRole())) {
			throw new ProductOperationException("您已經是賣家了");
		}
		verificationRepository.findByUser_UserId(applicantUserId).ifPresent(existing->{
			if(VerificationStatus.Pending.equals(existing.getStatus())||VerificationStatus.Approved.equals(existing.getStatus())) {
				throw new ProductOperationException("您已提交過賣家申請或申請已通過，狀態："+existing.getStatus());	
			}
		  // 如果是 Rejected 或 Resubmit，允許重新申請 (或更新現有申請)
      // 這裡簡單處理為創建新的，舊的可以標記為失效或不處理
		});
		
		SellerVerification verification =verificationMapper.toEntity(applyDto, applicant);
		verification.setStatus(VerificationStatus.Pending);
		SellerVerification savedVerification = verificationRepository.save(verification);	
		return verificationMapper.toDto(savedVerification);
	}

	@Override
	@Transactional(readOnly = true)
	public SellerVerificationDto getVerificationByIdForAdmin(Integer verificationId) {
		SellerVerification verification =verificationRepository.findById(verificationId)
				.orElseThrow(()->new ProductNotFoundException("找不到賣家資格審核申請 ID: "+verificationId));
		return verificationMapper.toDto(verification);
	}

	@Override
	@Transactional(readOnly = true)
	public SellerVerificationDto getUserVerificationStatus(Integer userId) throws UserNotFoundException {
		userRepository.findById(userId)
		.orElseThrow(()->new UserNotFoundException("用戶不存在 ID:"+userId));
		SellerVerification verification = verificationRepository.findByUser_UserId(userId)
				.orElseThrow(()->new ProductNotFoundException("該用戶尚未提交賣家資格審核申請"));
		return verificationMapper.toDto(verification);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SellerVerificationDto> getAllPendingVerificationsForAdmin() {
		 List<SellerVerification> verifications = verificationRepository
         .findByStatusOrderBySubmittedAtAsc(VerificationStatus.Pending);

 return verifications.stream().map(verification -> {
     // 手動初始化需要的懶加載關聯
     if (verification.getUser() != null) {
         Hibernate.initialize(verification.getUser()); // 初始化 User 代理
         // 如果 User 內部還有懶加載的，也需要處理
     }
     if (verification.getReviewedByAdmin() != null) {
         Hibernate.initialize(verification.getReviewedByAdmin()); // 初始化 reviewedByAdmin 代理
     }
     if (verification.getVerificationImages() != null) {
         Hibernate.initialize(verification.getVerificationImages()); // 初始化圖片集合代理
         // 如果 VerificationImage 內部還有懶加載，也需要處理
     }
     return verificationMapper.toDto(verification); // 現在調用 toDto
 }).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<SellerVerificationDto> getAllVerificationsForAdmin() {
		return verificationRepository.findAll()
				.stream()
				.map(verificationMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public SellerVerificationDto reviewVerification(Integer verifivationId, SellerVerificationReviewDto reviewDto,
			Integer adminUserId) throws UserNotFoundException {
		User admin = userRepository.findById(adminUserId)
				.orElseThrow(()->new UserNotFoundException("管理員用戶不存在 ID: "+adminUserId));
		if(!UserRole.ADMIN.equals(admin.getPrimaryRole())) {
		  throw new AccessDeniedException("只有管理員可以審核賣家資格");
		}
		SellerVerification verification = verificationRepository.findById(adminUserId)
				.orElseThrow(()->new ProductNotFoundException("找不到賣家資格審核申請 ID: "+verifivationId));
		
		verification.setStatus(reviewDto.getStatus());
		verification.setAdminRemarks(reviewDto.getAdminRemarks());
		verification.setReviewedByAdmin(admin);
		verification.setReviewedAt(LocalDateTime.now());
		
		// 如果審核通過，更新用戶角色
		if(VerificationStatus.Approved.equals(reviewDto.getStatus())) {
			User applicant =verification.getUser();
			applicant.setPrimaryRole(UserRole.SELLER);
			userRepository.save(applicant);
		}
		SellerVerification updateVerification	= verificationRepository.save(verification);
		return verificationMapper.toDto(updateVerification);
	}

}
