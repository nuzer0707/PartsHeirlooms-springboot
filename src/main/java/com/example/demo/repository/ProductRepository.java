package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.enums.ProductStatus;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
	
	//根據賣家用戶 ID 查找產品
	List<Product> findBySellerUser_UserId(Integer userId);
  
	// 根據分類 ID 查找產品
	List<Product> findByCategory_CategoryId(Integer category);
	
	// 根據產品狀態查找產品
	List<Product> findByStatus(ProductStatus status);
	
	//根據賣家用戶 ID 和產品狀態查找產品
	List<Product> findBySellerUser_UserIdAndStatus(Integer userId, ProductStatus status);
	
	 // 根據分類 ID 和產品狀態查找產品
	List<Product> findByCategory_CategoryIdAndStatus(Integer categoryId, ProductStatus status); 
	
	 // 範例：根據標題（來自 ProductContent）查找產品 (忽略大小寫)
	//這需要 JOIN，Spring Data JPA 通常可以推斷出來，或者您可以明確地編寫
	@Query("SELECT p FROM Product p JOIN p.productContent pc WHERE LOWER(pc.title) LIKE LOWER(concat('%', :keyword, '%'))")
	List<Product>findByTitleContainingIgnoreCase(@Param("keyword")String keyword);
	
	//根據產品 ID 和賣家 ID 查找產品，用於權限驗證
	Optional<Product> findByProductIdAndSellerUser_UserId(Integer productId,Integer sellerUserid);


}
