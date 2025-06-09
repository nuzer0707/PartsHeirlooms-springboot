package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.Rating;



@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer>{

	List<Rating> findByRatedUser_UserIdOrderByCreatedAtDesc(Integer userId);
  List<Rating> findByRaterUser_UserIdOrderByCreatedAtDesc(Integer userId);
  List<Rating> findByTransaction_ProductId_ProductIdOrderByCreatedAtDesc(Integer productId);
  Optional<Rating> findByTransaction_TransactionIdAndRaterUser_UserId(Integer transactionId, Integer raterId);
  boolean existsByTransaction_TransactionIdAndRaterUser_UserId(Integer transactionId, Integer raterId);

}
