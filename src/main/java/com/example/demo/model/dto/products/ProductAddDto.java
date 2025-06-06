package com.example.demo.model.dto.products;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductAddDto {

	@NotNull(message = "分類 ID 不能為空")
	private Integer categoryId;

	@NotNull(message = "價格不能為空")
	@DecimalMin(value = "1", inclusive = true, message = "價格必須大於或等於 1")
	private BigDecimal price;

	@NotNull(message = "價格不能為空")
	@Positive(message = "數量必須為正整數")
	private Integer quantity;

	@Valid
	@NotNull(message = "產品內容不能為空")
	private ProductContentDto content;

	@Valid
	@NotNull(message = "產品圖片列表不能為 null")
	@Size(min = 1, max = 6, message = "至少需要一張產品圖片，且不能超過六張")
	private List<ProductImageDto> images;// 產品圖片列表

	@Valid
	@NotNull(message = "交易方式列表不能為 null")
	@Size(min = 1, message = "至少需要一種交易方式")
	private List<ProductTransactionDetailInputDto> transactionDetails; // 交易明細列表

}
