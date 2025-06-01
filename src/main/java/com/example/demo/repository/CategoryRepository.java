package com.example.demo.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.Category;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{

	 /**
     * 根據分類名稱查找分類 (名稱是唯一的)。
     * @param name 分類名稱 (區分大小寫)
     * @return Optional<Category> 如果找到則包含分類，否則為空
     */
	
	Optional<Category> findByCategoryName(String categoryName);
	
    /**
     * 根據分類名稱查找分類 (忽略大小寫)。
     * @param name 分類名稱
     * @return Optional<Category> 如果找到則包含分類，否則為空
     */
	
	Optional<Category> findByCategoryNameIgnoreCase(String categoryName);
	
	/**
     * 查找名稱包含指定關鍵字的分類列表 (區分大小寫)。
     * 例如：傳入 "卡"，可以找到 "顯示卡"
     * @param keyword 關鍵字
     * @return 符合條件的分類列表
     */
	 List<Category> findByCategoryNameContaining(String keyword);
	 
	 /**
	     * 查找名稱包含指定關鍵字的分類列表 (忽略大小寫)。
	     * 例如：傳入 "cpu"，可以找到 "CPU" 或 "Cpu"
	     * @param keyword 關鍵字
	     * @return 符合條件的分類列表
	     */
	 
	 List<Category> findByCategoryNameContainingIgnoreCase(String keyword);
	 
	   /**
	     * 檢查是否存在具有指定名稱的分類 (區分大小寫)。
	     * 這對於在創建新分類前驗證名稱是否已存在很有用。
	     * @param name 分類名稱
	     * @return 如果存在則為 true，否則為 false
	     */
	 boolean existsByCategoryName(String categoryName);
	    /**
	     * 檢查是否存在具有指定名稱的分類 (忽略大小寫)。
	     * @param name 分類名稱
	     * @return 如果存在則為 true，否則為 false
	     */
	 boolean existsByCategoryNameIgnoreCase(String categoryName);
	 
	 // 如果你需要更複雜的查詢，可以使用 @Query 註解來編寫 JPQL 或原生 SQL
	 // 例如：
	 // @Query("SELECT c FROM Category c WHERE c.name LIKE %:keyword% AND LENGTH(c.name) > :minLength")
	 // List<Category> findByNameWithMinLength(@Param("keyword") String keyword, @Param("minLength") int minLength);
	 
}
