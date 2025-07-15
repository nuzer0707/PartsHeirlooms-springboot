package com.example.demo.repository;

import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.enums.ProductStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    //根據賣家用戶 ID 查找產品

    @EntityGraph(attributePaths = {"sellerUser", "category", "productContent", "productImages"})
    @Override
    List<Product> findAll();

    @EntityGraph(attributePaths = {"productContent","category", "productImages"})
    List<Product> findBySellerUser_UserId(Integer userId);

    // 根據分類 ID 查找產品
    List<Product> findByCategory_CategoryId(Integer category);

    // 根據產品狀態查找產品
    @EntityGraph(attributePaths = {"sellerUser", "category", "productContent", "productImages"})
    List<Product> findByStatus(ProductStatus status);

    //根據賣家用戶 ID 和產品狀態查找產品
    @EntityGraph(attributePaths = {"sellerUser", "category", "productContent"})
    List<Product> findBySellerUser_UserIdAndStatus(Integer userId, ProductStatus status);

    // 根據分類 ID 和產品狀態查找產品
    List<Product> findByCategory_CategoryIdAndStatus(Integer categoryId, ProductStatus status);

    // 範例：根據標題（來自 ProductContent）查找產品 (忽略大小寫)
    //這需要 JOIN，Spring Data JPA 通常可以推斷出來，或者您可以明確地編寫
    @Query("SELECT p FROM Product p JOIN p.productContent pc WHERE LOWER(pc.title) LIKE LOWER(concat('%', :keyword, '%'))")
    List<Product> findByTitleContainingIgnoreCase(@Param("keyword") String keyword);

    //根據產品 ID 和賣家 ID 查找產品，用於權限驗證
    Optional<Product> findByProductIdAndSellerUser_UserId(Integer productId, Integer sellerUserid);

    @EntityGraph(attributePaths = {"sellerUser", "category", "productContent", "productImages", "transactionDetails"})
    @Override
    Optional<Product> findById(Integer productId);


    /**
     * 計算具有特定狀態的產品數量。
     *
     * @param status 產品狀態
     * @return 符合條件的產品數量
     */
    long countByStatus(ProductStatus status); // 新增：Spring Data JPA 會自動實現這個查詢

    @Query("SELECT DISTINCT p FROM Product p " +
            "JOIN p.productContent pc " +
            "JOIN p.category c " +
            "WHERE (LOWER(pc.title) LIKE LOWER(concat('%', :keyword, '%')) OR LOWER(c.categoryName) LIKE LOWER(concat('%', :keyword, '%'))) " +
            "AND p.status = com.example.demo.model.entity.enums.ProductStatus.For_Sale")
    List<Product> findByTitleOrCategoryNameContainingIgnoreCaseAndStatusForSale(@Param("keyword") String keyword);


//    //一個通用的 JOIN FETCH 方法
//    @Query("SELECT p FROM Product p " + "LEFT JOIN FETCH p.sellerUser" +
//            " LEFT JOIN FETCH p.category" +
//            " LEFT JOIN FETCH p.productContent" +
//            " LEFT JOIN FETCH p.productImages" +
//            " WHERE p.productId = :productId")
//    Optional<Product> findByIdWithDetails(@Param("productId") Integer productId);
//
//    // 為了避免笛卡爾積問題，獲取集合時，先獲取主實體 ID，再用 ID 獲取完整實體
//    @Query("SELECT DISTINCT p FROM Product p " +
//            " LEFT JOIN FETCH p.sellerUser" +
//            " LEFT JOIN FETCH p.category" +
//            " LEFT JOIN FETCH p.productContent" +
//            " WHERE p.status = :status")
//    List<Product> findByStatusWithDetails(@Param("productId") ProductStatus status);
//
//    // 同理，為其他查詢也創建對應的 WithDetails 版本
//    @Query("SELECT DISTINCT p FROM Product p " +
//            " LEFT JOIN FETCH p.sellerUser" +
//            " LEFT JOIN FETCH p.category" +
//            " LEFT JOIN FETCH p.productContent" +
//            " WHERE p.sellerUser.userId = :userId AND p.status = :status")
//    List<Product> findBySellerUserIdAndStatusWithDetails(@Param("productId") Integer sellerUserid, @Param("productId") ProductStatus status);


}
