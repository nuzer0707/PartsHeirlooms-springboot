package com.example.demo.service;

import java.util.List;

import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.products.ProductSummaryDto;

public interface FavoriteService {

	
	void addFavorite(Integer userId ,Integer productId) throws UserNotFoundException;

	void removeFavorite(Integer userId,Integer productId);
	
	List<ProductSummaryDto>getFavoritesByUserId(Integer userId);
	
	boolean isFavorited(Integer userId,Integer productId);
	
}
