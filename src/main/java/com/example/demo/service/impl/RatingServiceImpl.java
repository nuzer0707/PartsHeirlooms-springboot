package com.example.demo.service.impl;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.ProductOperationException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.AccessDeniedException;

import com.example.demo.mapper.RatingMapper;
import com.example.demo.model.dto.RatingDto;
import com.example.demo.model.dto.RatingSubmitDto;
import com.example.demo.model.entity.Rating;
import com.example.demo.model.entity.Transaction;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.enums.UserRole;
import com.example.demo.repository.RatingRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.RatingService;

public class RatingServiceImpl implements RatingService{

	@Autowired
	private RatingRepository ratingRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private RatingMapper ratingMapper;
	
	
	
	
	@Override
	@Transactional
	public RatingDto addRating(RatingSubmitDto submitDto,Integer raterUserId) throws UserNotFoundException {
			User rater = userRepository.findById(raterUserId)
								.orElseThrow(()->new UserNotFoundException("評價者用戶不存在 ID: "+raterUserId));
			Transaction transaction  = transactionRepository.findById(submitDto.getTransactionId())
								.orElseThrow(()->new ProductNotFoundException("找不到交易 ID: "+submitDto.getTransactionId()));
			// 驗證評價者是否是該交易的買家
		   if (!transaction.getBuyerUser().getUserId().equals(raterUserId)) {
         throw new AccessDeniedException("只有交易的買家可以評價此交易");
     }

			
			// 驗證是否已評價過
			
			if(ratingRepository.existsByTransaction_TransactionIdAndRaterUser_UserId(transaction.getTransactionId(), raterUserId)) {
				throw new ProductOperationException("您已經評價過此交易");
			}
			
			User ratedUser = transaction.getSellerUser(); // 評價對象是賣家
			
			Rating rating= ratingMapper.toEntity(submitDto, rater, ratedUser, transaction);
			Rating savedRating = ratingRepository.save(rating);
			return ratingMapper.toDto(savedRating);
			
		
	}

	@Override
	@Transactional(readOnly = true)
	public RatingDto getRatingById(Integer ratingId) {
		Rating rating = ratingRepository.findById(ratingId)
				.orElseThrow(()->new ProductNotFoundException("找不到評價 ID:" +ratingId));
		return ratingMapper.toDto(rating);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RatingDto> getRatingsByRatedUser(Integer ratedUserId) throws UserNotFoundException {
		if(!userRepository.existsById(ratedUserId)) {
			throw new UserNotFoundException("找不到被評價用戶 ID: "+ratedUserId);
		}
		return ratingRepository
						.findByRatedUser_UserIdOrderByCreatedAtDesc(ratedUserId)
						.stream()
						.map(ratingMapper::toDto)
						.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<RatingDto> getRatingsByProduct(Integer productId) {
		 // 這裡的邏輯是獲取與此產品相關交易的所有評價 (即對賣家的評價)
		return ratingRepository
					.findByTransaction_ProductId_ProductIdOrderByCreatedAtDesc(productId)
					.stream()
					.map(ratingMapper::toDto)
					.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<RatingDto> getAllRatingsForAdmin() {
		return ratingRepository
				.findAll()
				.stream()
				.map(ratingMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteRatingByAdmin(Integer ratindId, Integer adminUserId) throws UserNotFoundException {
		User admin = userRepository.findById(adminUserId)
								.orElseThrow(()->new UserNotFoundException("管理員用戶不存在 ID: "+adminUserId));
		if(!UserRole.ADMIN.equals(admin.getPrimaryRole())) {
			throw new AccessDeniedException("只有管理員可以刪除評價");
		}
		if(!ratingRepository.existsById(ratindId)) {
			throw new ProductNotFoundException("找不到要刪除的評價 ID:"+ratindId);
		}
		ratingRepository.deleteById(ratindId);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean hasUserRatedTransaction(Integer transactionId, Integer raterUserId) {
		return ratingRepository
				.existsByTransaction_TransactionIdAndRaterUser_UserId(transactionId, raterUserId);
	}

}
