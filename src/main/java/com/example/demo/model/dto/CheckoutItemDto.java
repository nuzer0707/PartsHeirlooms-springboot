package com.example.demo.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutItemDto {
	@NotNull(message = "產品 ID 不能為空")
	private Integer productId;

	@NotNull(message = "必須選擇交易方式")
	private Integer chosenTransactionMethodId;
}
