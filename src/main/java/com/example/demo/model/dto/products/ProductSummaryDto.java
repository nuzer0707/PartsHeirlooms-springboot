package com.example.demo.model.dto.products;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.model.entity.enums.ProductStatus;

import lombok.Data;

@Data
public class ProductSummaryDto {
	//產品概要 DTO (列表優化)
	private Integer productId;
    private Integer sellerUserId;
    private String sellerUsername;
	private Integer categoryId;
	private String categoryName;
	private BigDecimal price;
	private ProductStatus status;
	private LocalDateTime createdAt;
	private String title;
	private String shortDescription;
	private String firstBase64Image;// 只包含第一張圖片的 Base64 字串作為預覽
}
