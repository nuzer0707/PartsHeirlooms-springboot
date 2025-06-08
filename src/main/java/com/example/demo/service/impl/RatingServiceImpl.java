package com.example.demo.service.impl;

import java.util.List;

import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.RatingDto;
import com.example.demo.model.dto.RatingSubmitDto;
import com.example.demo.service.RatingService;

public class RatingServiceImpl implements RatingService{

	@Override
	public RatingDto addRating(RatingSubmitDto submitDto, Integer raterUserId) throws UserNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RatingDto getRatingById(Integer ratingId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RatingDto> getRatingsByRatedUser(Integer ratedUserId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RatingDto> getRatingsByProduct(Integer productId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RatingDto> getAllRatingsForAdmin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteRatingByAdmin(Integer ratindId, Integer adminUserId) throws UserNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasUserRatedTransaction(Integer transactionId, Integer raterUserId) {
		// TODO Auto-generated method stub
		return false;
	}

}
