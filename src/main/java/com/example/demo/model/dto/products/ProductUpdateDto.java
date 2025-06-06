package com.example.demo.model.dto.products;

import java.math.BigDecimal;
import java.util.List;

import com.example.demo.model.entity.enums.ProductStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductUpdateDto {

	private Integer categoryId;
	
	@DecimalMin(value = "1", inclusive = true, message = "價格必須大於或等於 1")
	private BigDecimal price;
	
	@Positive(message = "數量必須為正整數")
	private Integer quantity;
	
	
	private ProductStatus status;
	
	private ProductContentDto content;
	
	@Valid
	private List<ProductImageDto> images;
	
	@Valid
	private List<ProductTransactionDetailInputDto> transactionDetails;
	
}
