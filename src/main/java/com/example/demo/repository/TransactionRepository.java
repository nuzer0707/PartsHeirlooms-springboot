package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

	List<Transaction> findByBuyerUserUserIdOrderByCreatedAtDesc(Integer buyerId);

	List<Transaction> findBySellerUserUserIdOrderByCreatedAtDesc(Integer sellerId);

	Optional<Transaction> findByTransactionIdAndBuyerUserUserId(Integer transactionId, Integer buyerId);

	Optional<Transaction> findByTransactionIdAndSellerUserUserId(Integer transactionId, Integer sellerId);

}
