package com.example.demo.mapper;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.TransactionMethod;

public interface TransactionMethodRepository extends JpaRepository<TransactionMethod, Integer>{

}
