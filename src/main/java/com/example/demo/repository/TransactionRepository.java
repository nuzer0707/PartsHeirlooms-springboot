package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.Transaction;
import com.example.demo.model.entity.enums.TransactionStatus;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

	List<Transaction> findByBuyerUserUserIdOrderByCreatedAtDesc(Integer buyerId);

	List<Transaction> findBySellerUserUserIdOrderByCreatedAtDesc(Integer sellerId);

	Optional<Transaction> findByTransactionIdAndBuyerUserUserId(Integer transactionId, Integer buyerId);

	Optional<Transaction> findByTransactionIdAndSellerUserUserId(Integer transactionId, Integer sellerId);

	// --- 新增方法 ---
  /**
   * 查找具有特定狀態的所有交易。
   * @param status 交易狀態
   * @return 符合條件的交易列表
   */
  List<Transaction> findByStatus(TransactionStatus status); // 新增：Spring Data JPA 會自動實現這個查詢
	
}
