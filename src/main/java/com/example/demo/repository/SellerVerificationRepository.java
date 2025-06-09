package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.SellerVerification;
import com.example.demo.model.entity.enums.VerificationStatus;

@Repository
public interface SellerVerificationRepository extends JpaRepository<SellerVerification,Integer> {
	 Optional<SellerVerification> findByUser_UserId(Integer userId);
   List<SellerVerification> findByStatusOrderBySubmittedAtAsc(VerificationStatus status);

}
