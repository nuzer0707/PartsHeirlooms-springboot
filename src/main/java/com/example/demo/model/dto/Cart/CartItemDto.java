package com.example.demo.model.dto.Cart;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CartItemDto {

	private Integer productId;
	
	private String productTitle;
	
	private String productFirstImageBase64;
	
	private BigDecimal price;
	
  private Integer quantity;
	
	private BigDecimal subtotal;// 小計 (price * quantity)
	
}
