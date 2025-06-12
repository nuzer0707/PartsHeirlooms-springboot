package com.example.demo.model.dto.Cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemUpdateDto {

  @NotNull(message = "數量不能為空")
  @Min(value = 1, message = "數量至少為 1")
  private Integer quantity;

	
}
