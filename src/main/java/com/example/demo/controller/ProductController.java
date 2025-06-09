package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.CategoryNotFoundException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.dto.products.ProductDto;
import com.example.demo.model.dto.products.ProductSummaryDto;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.ProductService;

@RestController
@RequestMapping(value = {"/products"})
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8002"},allowCredentials = "true")
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	
	// 無登入使用者 / 買家 可見 (公開列表)
	@GetMapping
	public ResponseEntity<ApiResponse<List<ProductSummaryDto>>> getAllPublicProducts(){
		
		List<ProductSummaryDto> products = productService.getAllPublicProduct();
		
		if(products.isEmpty()) {
			return ResponseEntity.ok(ApiResponse.success("目前沒有可供瀏覽的商品", products));
		}
		return ResponseEntity.ok(ApiResponse.success("查詢公開商品列表成功", products));
	}
	
	//無登入使用者 / 買家 可見 (公開詳情)
	@GetMapping("/{productId}")
	public ResponseEntity<ApiResponse<ProductDto>> getPublicProductById(
			@PathVariable Integer productId
			){
		try {
			// 這個方法內部應只返回 For_Sale 的商品
			ProductDto productDto = productService.getProuctById(productId); 
			return ResponseEntity.ok(ApiResponse.success("查詢商品詳情成功", productDto));			
		} catch (ProductNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
		}
	}
	
	// 按分類查詢商品 (公開)
	@GetMapping("/category/{categoryId}")
	public ResponseEntity<ApiResponse<List<ProductSummaryDto>>> getProductsByCategory(
		@PathVariable Integer categoryId
		){
		try {
			List<ProductSummaryDto> products =
				productService.getProductsByCategory(categoryId);
			if(products.isEmpty()) {
				return ResponseEntity.ok(ApiResponse.success("此分類下尚無商品", products));
			}
			return ResponseEntity.ok(ApiResponse.success("依分類查詢商品成功", products));
		} catch (CategoryNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
		}
		
	}
	

    // 按關鍵字搜索商品 (公開)
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductSummaryDto>>> searchProducts(
    		@RequestParam String keyword){
    	List<ProductSummaryDto> products = 
    			productService.findProductsByTitle(keyword);
    	if(products.isEmpty()) {
    		return ResponseEntity.ok(ApiResponse.success("找不到與 '" + keyword + "' 相關的商品", products));
    	}
    	return ResponseEntity.ok(ApiResponse.success("商品搜索成功",products));
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
