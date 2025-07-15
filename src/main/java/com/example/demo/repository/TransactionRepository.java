package com.example.demo.repository;

import com.example.demo.model.entity.Transaction;
import com.example.demo.model.entity.enums.TransactionStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

	List<Transaction> findByBuyerUserUserIdOrderByCreatedAtDesc(Integer buyerId);

	List<Transaction> findBySellerUserUserIdOrderByCreatedAtDesc(Integer sellerId);

	Optional<Transaction> findByTransactionIdAndBuyerUserUserId(Integer transactionId, Integer buyerId);

	Optional<Transaction> findByTransactionIdAndSellerUserUserId(Integer transactionId, Integer sellerId);

	List<Transaction> findByStatus(TransactionStatus status); // 新增：Spring Data JPA 會自動實現這個查詢
	
	// --- 新增方法 ---
    /**
     * 根據買家ID和一組交易狀態查詢交易。
     * @param buyerId 買家用戶ID
     * @param statuses 一個或多個交易狀態
     * @return 符合條件的交易列表，按創建時間降序排列
     */

	@Query("SELECT t FROM Transaction t WHERE t.buyerUser.userId = :buyerId AND t.status IN :statuses ORDER BY t.createdAt DESC")
	@EntityGraph(attributePaths = {"shipmentDetails", "productId","buyerUser", "sellerUser"})
	List<Transaction> findByBuyerUserUserIdAndStatusInOrderByCreatedAtDesc(@Param("buyerId") Integer buyerId, @Param("statuses") List<TransactionStatus> statuses);
	
	/**
     * 根據賣家ID和一組交易狀態查詢交易。
     * @param sellerId 賣家用戶ID
     * @param statuses 一個或多個交易狀態
     * @return 符合條件的交易列表，按創建時間降序排列
     */

	@Query("SELECT t FROM Transaction t WHERE t.sellerUser.userId = :sellerId AND t.status IN :statuses ORDER BY t.createdAt DESC")
	@EntityGraph(attributePaths = {"shipmentDetails", "productId","buyerUser", "sellerUser"})
	List<Transaction> findBySellerUserUserIdAndStatusInOrderByCreatedAtDesc(@Param("sellerId") Integer sellerId, @Param("statuses") List<TransactionStatus> statuses);

	
	
	boolean existsByProductId_ProductId(Integer productId);
	
	
	 boolean existsByProductId_ProductIdAndStatusIn(Integer productId, List<TransactionStatus> statuses);
}
