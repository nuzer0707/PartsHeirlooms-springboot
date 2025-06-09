package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

	List<Rating> findByRatedUserId_UserIdOrderByCreatedAtDesc(Integer userId);

	List<Rating> findByRaterUserId_UserIdOrderByCreatedAtDesc(Integer userId);

	List<Rating> findByTransaction_ProductId_ProductIdOrderByCreatedAtDesc(Integer productId);

	Optional<Rating> findByTransaction_TransactionIdAndRaterUserId_UserId(Integer transactionId, Integer raterId);

	boolean existsByTransaction_TransactionIdAndRaterUserId_UserId(Integer transactionId, Integer raterId);

}
