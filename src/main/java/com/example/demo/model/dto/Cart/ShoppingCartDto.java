package com.example.demo.model.dto.Cart;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class ShoppingCartDto {
	
	private List<CartItemDto> items;
	
	private BigDecimal totalPrice;
	
}
