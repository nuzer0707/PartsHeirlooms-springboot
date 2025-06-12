package com.example.demo.model.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CheckoutRequestDto {
	@NotEmpty(message = "結帳項目不能為空")
	@Valid
	private List<CheckoutItemDto> items;

	// 運送資訊，對於「面交」可以為 null，所以不需要 @NotNull
	@Valid
	private ShippingInfoDto shippingInfo;
}
