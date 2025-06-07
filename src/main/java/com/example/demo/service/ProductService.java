package com.example.demo.service;

import java.util.List;

import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.products.ProductAddDto;
import com.example.demo.model.dto.products.ProductDto;
import com.example.demo.model.dto.products.ProductSummaryDto;
import com.example.demo.model.dto.products.ProductUpdateDto;
import com.example.demo.model.entity.enums.ProductStatus;
import com.example.demo.model.entity.enums.UserRole;

public interface ProductService {

	// 獲取所有公開可見的產品 (例如，狀態為 "For_Sale" 的產品)
	List<ProductSummaryDto> getAllPublicProduct();

	// 供管理員獲取所有產品 (無論狀態如何)
	List<ProductSummaryDto> getAllProductsFotAdmin();

	// 根據 ID 獲取產品 (如果產品狀態為 "For_Sale"，則公開可見)
	ProductDto getProuctById(Integer productId);

	// 供產品擁有者或管理員根據 ID 獲取產品 (可查看任何狀態的產品)
	ProductDto getProductByIdForOwnerOrAdmin(Integer productId, Integer requestingUserId, UserRole requestingUserRole);

	// 根據賣家 ID 獲取產品 (預設只顯示 "For_Sale" 狀態)
	List<ProductSummaryDto> getProductsBySeller(Integer sellerUserId) throws UserNotFoundException;

	// 根據分類 ID 獲取產品 (預設只顯示 "For_Sale" 狀態)
	List<ProductSummaryDto> getProductsByCategory(Integer categoryId);

	// 根據產品標題關鍵字查找產品 (預設只顯示 "For_Sale" 狀態)
	List<ProductSummaryDto> findProductsByTitle(String keywork);

	// 創建新產品 (賣家 ID 從 session 獲取)
	ProductDto addProduct(ProductAddDto addDto, Integer sellerUserId) throws UserNotFoundException;

	// 更新現有產品 (需要當前用戶 ID 和角色進行權限驗證)
	ProductDto updateProduct(Integer productId, ProductUpdateDto updateDto, Integer currentUserId,
			UserRole currentUserRole);
	// 刪除產品 (需要當前用戶 ID 和角色進行權限驗證)
	void deleteProduct(Integer productId,Integer currentUserId,UserRole currentUserRole);
	
	// 更新產品狀態 (需要當前用戶 ID 和角色進行權限驗證)
	ProductDto updateProductStatus(Integer productId,ProductStatus newStatus,Integer currentUserId,UserRole currentUserRole);







}
