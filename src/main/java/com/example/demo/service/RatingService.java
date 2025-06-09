package com.example.demo.service;


import java.util.List;

import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.RatingDto;
import com.example.demo.model.dto.RatingSubmitDto;

public interface RatingService {

	RatingDto addRating(RatingSubmitDto submitDto,Integer raterUserId)throws UserNotFoundException;
	RatingDto getRatingById(Integer ratingId);
	List<RatingDto> getRatingsByRatedUser(Integer ratedUserId) throws UserNotFoundException;
	List<RatingDto> getRatingsByProduct(Integer productId);// 買家對此產品賣家的所有評價
	List<RatingDto> getAllRatingsForAdmin();
	void deleteRatingByAdmin(Integer ratindId,Integer adminUserId)throws UserNotFoundException;
	// 買家是否已評價過此交易
	boolean hasUserRatedTransaction(Integer transactionId,Integer raterUserId);

}
