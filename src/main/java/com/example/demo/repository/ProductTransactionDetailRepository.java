package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.ProductTransactionDetail;

@Repository
public interface ProductTransactionDetailRepository extends JpaRepository<ProductTransactionDetail, Integer> {

}
