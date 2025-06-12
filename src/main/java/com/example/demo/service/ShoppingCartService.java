package com.example.demo.service;

import com.example.demo.model.dto.Cart.CartItemAddDto;
import com.example.demo.model.dto.Cart.ShoppingCartDto;



public interface ShoppingCartService {

  ShoppingCartDto getCart(Integer userId);

  ShoppingCartDto addItemToCart(Integer userId, CartItemAddDto itemDto);

  ShoppingCartDto updateItemQuantity(Integer userId, Integer productId, Integer quantity);

  ShoppingCartDto removeItemFromCart(Integer userId, Integer productId);

  void clearCart(Integer userId);

}
