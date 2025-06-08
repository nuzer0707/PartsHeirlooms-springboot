package com.example.demo.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.ProductOperationException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.dto.products.ProductSummaryDto;
import com.example.demo.model.entity.Favorite;
import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.User;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FavoriteService;

@Service
public class FavoriteServiceImpl implements FavoriteService {
	
	@Autowired
	private FavoriteRepository favoriteRepository; 
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ProductMapper productMapper;
	
	@Override
	@Transactional
	public void addFavorite(Integer userId, Integer productId) throws UserNotFoundException {
		if(favoriteRepository.existsByUserUserIdAndProductProductId(userId, productId)) {
			throw new ProductOperationException("此產品已在您的收藏列表中");
		}
		
		User user =userRepository.findById(userId)
				.orElseThrow(()->new UserNotFoundException("找不到用戶 ID: " + userId));
		Product product = productRepository.findById(productId)
				.orElseThrow(()->new ProductNotFoundException("找不到產品 ID: " + productId));
		
		Favorite favorite = Favorite.builder()
				.user(user)
				.product(product)
				.build();
	
		favoriteRepository.save(favorite);
	}

	@Override
	@Transactional
	public void removeFavorite(Integer userId, Integer productId) {
		
		favoriteRepository.deleteByUserUserIdAndProductProductId(userId, productId);
		
	}

	
	@Override
	@Transactional(readOnly = true)
	public List<ProductSummaryDto> getFavoritesByUserId(Integer userId) {
		List<Favorite> favorites = favoriteRepository.findByUserUserId(userId);
		
		return favorites.stream()
				.map(Favorite::getProduct)
				.map(productMapper::toSummaryDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isFavorited(Integer userId, Integer productId) {
		return favoriteRepository.existsByUserUserIdAndProductProductId(userId, productId);
	}

	
}
