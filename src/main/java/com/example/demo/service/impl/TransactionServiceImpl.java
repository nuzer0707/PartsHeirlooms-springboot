package com.example.demo.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.ProductOperationException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.mapper.TransactionMapper;
import com.example.demo.model.dto.TransactionDto;
import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.ProductTransactionDetail;
import com.example.demo.model.entity.Transaction;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.enums.ProductStatus;
import com.example.demo.model.entity.enums.TransactionStatus;
import com.example.demo.model.entity.enums.UserRole;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductTransactionDetailRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService{
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProductTransactionDetailRepository productTransactionDetailRepository;
	
	@Autowired
	private TransactionMapper transactionMapper;
	
	
	
	@Override
	@Transactional
	public TransactionDto addTransaction(Integer buyerId, Integer productId, Integer chosenTransactionDetailId) throws UserNotFoundException {
		User buyer = userRepository.findById(buyerId)
				.orElseThrow(()->new UserNotFoundException("找不到買家 ID: " + buyerId));
		Product product = productRepository.findById(productId)
				.orElseThrow(()->new ProductNotFoundException("找不到產品 ID: " + productId));
		ProductTransactionDetail productTransactionDetail = 
				productTransactionDetailRepository.findById(chosenTransactionDetailId)
				.orElseThrow(()->new ProductOperationException("無效的交易方式明細 ID: " + chosenTransactionDetailId));
		
		if(product.getStatus()!=ProductStatus.For_Sale) {
			throw new ProductOperationException("產品 " + product.getProductContent().getTitle() + " 目前非待售狀態");
		}
		
		if(product.getQuantity()<=0) {
			throw new ProductOperationException("產品 " + product.getProductContent().getTitle() + " 已售罄");
		}
		
		if(product.getSellerUser().getUserId().equals(buyerId)) {
			throw new ProductOperationException("您不能購買自己的產品");
		}
		
		Transaction transaction =Transaction.builder()
				.productId(product)
				.buyerUser(buyer)
				.sellerUser(product.getSellerUser())
				.chosenTransactionDetail(productTransactionDetail)
				.finalPrice(product.getPrice())
				.build();
		
		productRepository.save(product);
		
		Transaction savaedTransaction = transactionRepository.save(transaction);
		
		return transactionMapper.toDto(savaedTransaction);
	}

	@Override
	@Transactional(readOnly = true)
	public TransactionDto getTransactionById(Integer transactionId, Integer requestingUserId, UserRole userRole) {
		
		Transaction transaction = transactionRepository.findById(transactionId)
					.orElseThrow(()->new ProductNotFoundException("找不到交易 ID: " + transactionId));
		
		boolean isAdmin = UserRole.ADMIN.equals(userRole);
		boolean isBuyer = transaction.getBuyerUser().getUserId().equals(requestingUserId);
		boolean isSeller = transaction.getSellerUser().getUserId().equals(requestingUserId);
		
		if(!isAdmin && !isBuyer && !isSeller) {
			throw new AccessDeniedException("您無權查看此交易的詳細資訊");
		}
		return transactionMapper.toDto(transaction);
	}

	
	@Override
	@Transactional(readOnly = true)
	public List<TransactionDto> getTransactionsByBuyer(Integer buyserId) {
		return transactionRepository.findByBuyerUserUserIdOrderByCreatedAtDesc(buyserId)
			   .stream()
			   .map(transactionMapper::toDto)
			   .collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<TransactionDto> getTransactionsBySeller(Integer sellerId) {
		return transactionRepository.findBySellerUserUserIdOrderByCreatedAtDesc(sellerId)
			   .stream()
			   .map(transactionMapper::toDto)
			   .collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<TransactionDto> getAllTransactionsForAdmin() {
		return transactionRepository.findAll()
			   .stream()
			   .map(transactionMapper::toDto)
			   .collect(Collectors.toList());
	}

	@Override
	@Transactional
	public TransactionDto updateTransactionsStatus(Integer transactionId, TransactionStatus newStatus,
			Integer requestingUserId, UserRole userRole) {
		Transaction transaction = transactionRepository.findById(transactionId)
				.orElseThrow(()->new ProductNotFoundException("找不到交易 ID: " + transactionId));
		boolean isAdmin = UserRole.ADMIN.equals(userRole);
		boolean isSeller = transaction.getSellerUser().getUserId().equals(requestingUserId);
		
		if(!isAdmin && !isSeller) {
			throw new AccessDeniedException("您無權更新此交易的狀態");
		}
		
		transaction.setStatus(newStatus);
		Transaction upTransaction = transactionRepository.save(transaction);
		
		return transactionMapper.toDto(upTransaction);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TransactionDto> getTransactionsByBuyerAndStatuses(Integer buyerId, List<TransactionStatus> statuses) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TransactionDto> getTransactionsBySellerAndStatuses(Integer sellerId, List<TransactionStatus> statuses) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransactionDto buyerCancelTransaction(Integer transactionId, Integer buyerUserId)
			throws UserNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

}
