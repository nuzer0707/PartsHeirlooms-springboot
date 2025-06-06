package com.example.demo.model.dto.products;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.model.entity.ProductImage;
import com.example.demo.model.entity.ProductTransactionDetail;
import com.example.demo.model.entity.enums.ProductStatus;

import lombok.Data;

@Data
public class ProductDto {

	private Integer productId; // 產品 ID
	
	private Integer sellerUserId; // 賣家用戶 ID
	
	private String sellerUsername;// 賣家用戶名
	
	private Integer categoryId;// 分類
	
	private String categoryName;// 分類名
	
	private BigDecimal price;  // 價格
	
	private Integer quantity;  // 數量
	
	private ProductStatus status; // 產品狀態
	
	private LocalDateTime createdAt; // 創建時間
	
  // 扁平化的內容
	
	private String title; // 標題
	
	private String shortDescription; // 簡短描述
	
	private String fullDescription; // 完整描述
	
	private List<ProductImage> productImages;
	
	private List<ProductTransactionDetail> transactionDetails;
	
	
	
}
