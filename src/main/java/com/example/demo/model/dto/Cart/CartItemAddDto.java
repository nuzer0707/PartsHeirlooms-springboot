package com.example.demo.model.dto.Cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemAddDto {
	
	
	@NotNull(message = "產品 ID 不能為空")
	private Integer productId;
	
	@NotNull(message = "不能是null")
	@Min(value = 1, message = "數量至少為 1")
	private Integer quantity;
	
}
