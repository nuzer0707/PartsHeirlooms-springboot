package com.example.demo.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.RatingDto;
import com.example.demo.model.dto.RatingSubmitDto;
import com.example.demo.model.entity.Rating;
import com.example.demo.model.entity.Transaction;
import com.example.demo.model.entity.User;

@Component
public class RatingMapper {

	@Autowired
	private ModelMapper modelMapper;
	
	public RatingDto toDto(Rating rating) {
		if(rating==null) {
			return null;
		}
		RatingDto dto = modelMapper.map(rating, RatingDto.class);
		 // 1. 處理「評價者 (Rater)」的資訊
		if(rating.getRaterUserId()!=null) {
			dto.setRaterUserId(rating.getRaterUserId().getUserId());
			dto.setRaterUsername(rating.getRaterUserId().getUsername());
		}
		 // 2. 處理「被評價者 (Rated)」的資訊
		if(rating.getRatedUserId()!=null) {
			dto.setRatedUserId(rating.getRatedUserId().getUserId());
			dto.setRatedUsername(rating.getRatedUserId().getUsername());
		}
		if(rating.getTransaction()!=null) {
			dto.setTransactionId(rating.getTransaction().getTransactionId());
		}
		return dto;
	}
	public Rating toEntity(RatingSubmitDto submitDto,User raterUser,User ratedUser,Transaction transaction) {
		if(submitDto==null) {
			return null;
		}
		Rating rating = new Rating();
		rating.setScore(submitDto.getScore());
		rating.setComment(submitDto.getComment());
		rating.setRatedUserId(ratedUser);
		rating.setRaterUserId(raterUser);
		rating.setTransaction(transaction);
		
		return rating;
		
	}
}
