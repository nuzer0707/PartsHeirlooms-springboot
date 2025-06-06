package com.example.demo.service;

import java.util.List;

import com.example.demo.model.dto.products.ProductDto;
import com.example.demo.model.dto.products.ProductSummaryDto;

public interface ProductService {

	//獲取所有公開可見的產品 (例如，狀態為 "For_Sale" 的產品)
	List<ProductSummaryDto> getAllPublicProduct();
	
	//供管理員獲取所有產品 (無論狀態如何)
	List<ProductSummaryDto> getAllProductsFotAdmin();
	
  // 根據 ID 獲取產品 (如果產品狀態為 "For_Sale"，則公開可見)
	ProductDto getProuctById(Integer productId);
	
	
	
	
	
}
