package com.example.demo.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.ProductOperationException;
import com.example.demo.mapper.ShoppingCartMapper;
import com.example.demo.model.dto.Cart.CartItemAddDto;
import com.example.demo.model.dto.Cart.ShoppingCartDto;
import com.example.demo.model.entity.CartItem;
import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.User;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ShoppingCartService;


@Service
public class ShoppingCartServiceImpl implements ShoppingCartService{
	
	@Autowired
	private CartItemRepository	cartItemRepository; 
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ShoppingCartMapper shoppingCartMapper;
	
	
	@Override
	@Transactional(readOnly = true)
	public ShoppingCartDto getCart(Integer userId) {
		 // 1. 業務邏輯：從資料庫獲取該使用者的所有購物車項目
    List<CartItem> cartItems = cartItemRepository.findByUser_UserId(userId);
    // 2. 轉換邏輯：將整個列表直接交給 Mapper 處理
    
    return shoppingCartMapper.toDto(cartItems);

	}

	@Override
	@Transactional
	public ShoppingCartDto addItemToCart(Integer userId, CartItemAddDto itemDto) {
		User user = userRepository.findById(userId)
				.orElseThrow(()->new ProductNotFoundException("找不到使用者 ID: " + userId));

		Product product = productRepository.findById(itemDto.getProductId())
        .orElseThrow(() -> new ProductNotFoundException("找不到產品 ID: " + itemDto.getProductId()));

	  Optional<CartItem> existingItemOpt = cartItemRepository.findByUser_UserIdAndProduct_ProductId(userId, itemDto.getProductId());
		
	  if(existingItemOpt.isPresent()) {
	  	
	  	CartItem existingItem  = existingItemOpt.get();
	  	int newQuantity = existingItem.getQuantity()+itemDto.getQuantity();
	  	if(product.getQuantity()<newQuantity) {
	  		throw new ProductOperationException("庫存不足，剩餘庫存：" + product.getQuantity());
	  	}
	  	existingItem.setQuantity(newQuantity);
	  	cartItemRepository.save(existingItem);
	  }else {
	  	CartItem newItem = new CartItem();
	  	newItem.setUser(user);
	  	newItem.setProduct(product);
	  	newItem.setQuantity(itemDto.getQuantity());
	  	cartItemRepository.save(newItem);  	
		}
	  
		return getCart(userId);
	}

	@Override
	@Transactional
	public ShoppingCartDto updateItemQuantity(Integer userId, Integer productId, Integer quantity) {
		Product product = productRepository.findById(productId)
        .orElseThrow(() -> new ProductNotFoundException("找不到產品 ID: " + productId));
		
		if(product.getQuantity()<quantity) {
			 throw new ProductOperationException("庫存不足，剩餘庫存：" + product.getQuantity());
		}

		CartItem cartItem = cartItemRepository.findByUser_UserIdAndProduct_ProductId(userId, productId)
				.orElseThrow(()-> new ProductNotFoundException("購物車中找不到此商品"));
		
		cartItem.setQuantity(quantity);
		cartItemRepository.save(cartItem);

		return getCart(userId);
	}

	@Override
	@Transactional
	public ShoppingCartDto removeItemFromCart(Integer userId, Integer productId) {

		 CartItem cartItem = cartItemRepository.findByUser_UserIdAndProduct_ProductId(userId, productId)
         .orElseThrow(() -> new ProductNotFoundException("購物車中找不到要移除的商品"));
		 
		 cartItemRepository.delete(cartItem);
		
		return getCart(userId);
	}

	@Override
	@Transactional
	public void clearCart(Integer userId) {
		cartItemRepository.deleteByUser_UserId(userId);
	}

}
