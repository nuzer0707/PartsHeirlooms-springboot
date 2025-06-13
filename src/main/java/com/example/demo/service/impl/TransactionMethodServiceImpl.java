package com.example.demo.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.mapper.TransactionMethodMapper;
import com.example.demo.model.dto.TransactionMethodDto;
import com.example.demo.repository.TransactionMethodRepository;
import com.example.demo.service.TransactionMethodService;

@Service
public class TransactionMethodServiceImpl implements TransactionMethodService {
	@Autowired
	private TransactionMethodRepository transactionMethodRepository;

	@Autowired
	private TransactionMethodMapper transactionMethodMapper; // 注入 Mapper

	@Override
	@Transactional(readOnly = true) // 這是唯讀操作，可以優化效能
	public List<TransactionMethodDto> getAllTransactionMethods() {
		return transactionMethodRepository.findAll().stream().map(transactionMethodMapper::toDto)
				.collect(Collectors.toList());
	}
}