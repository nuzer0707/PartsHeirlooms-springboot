package com.example.demo.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.ProductOperationException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.mapper.TransactionMapper;
import com.example.demo.model.dto.ShippingInfoDto;
import com.example.demo.model.dto.TransactionDto;
import com.example.demo.model.dto.checkout.CheckoutItemDto;
import com.example.demo.model.dto.checkout.CheckoutRequestDto;
import com.example.demo.model.entity.CartItem;
import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.ProductTransactionDetail;
import com.example.demo.model.entity.Transaction;
import com.example.demo.model.entity.TransactionMethod;
import com.example.demo.model.entity.TransactionShipmentDetail;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.enums.ProductStatus;
import com.example.demo.model.entity.enums.TransactionStatus;
import com.example.demo.model.entity.enums.UserRole;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductTransactionDetailRepository;
import com.example.demo.repository.TransactionMethodRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductTransactionDetailRepository productTransactionDetailRepository;
	
	@Autowired
	private CartItemRepository cartItemRepository;
	
	@Autowired
	private TransactionMapper transactionMapper;
	
	@Autowired
	private TransactionMethodRepository transactionMethodRepository;
	

	@Override
	@Transactional
	public TransactionDto addTransaction(Integer buyerId, Integer productId, Integer chosenTransactionDetailId)
			throws UserNotFoundException {
		User buyer = userRepository.findById(buyerId)
				.orElseThrow(() -> new UserNotFoundException("找不到買家 ID: " + buyerId));
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("找不到產品 ID: " + productId));
		ProductTransactionDetail productTransactionDetail = productTransactionDetailRepository
				.findById(chosenTransactionDetailId)
				.orElseThrow(() -> new ProductOperationException("無效的交易方式明細 ID: " + chosenTransactionDetailId));

		if (product.getStatus() != ProductStatus.For_Sale) {
			throw new ProductOperationException("產品 " + product.getProductContent().getTitle() + " 目前非待售狀態");
		}

		if (product.getQuantity() <= 0) {
			throw new ProductOperationException("產品 " + product.getProductContent().getTitle() + " 已售罄");
		}

		if (product.getSellerUser().getUserId().equals(buyerId)) {
			throw new ProductOperationException("您不能購買自己的產品");
		}

		Transaction transaction = Transaction.builder().productId(product).buyerUser(buyer)
				.sellerUser(product.getSellerUser()).chosenTransactionDetail(productTransactionDetail)
				.finalPrice(product.getPrice()).build();
		
		
		productRepository.save(product);

		Transaction savaedTransaction = transactionRepository.save(transaction);

		return transactionMapper.toDto(savaedTransaction);
	}

	@Override
	@Transactional(readOnly = true)
	public TransactionDto getTransactionById(Integer transactionId, Integer requestingUserId, UserRole userRole) {

		Transaction transaction = transactionRepository.findById(transactionId)
				.orElseThrow(() -> new ProductNotFoundException("找不到交易 ID: " + transactionId));

		boolean isAdmin = UserRole.ADMIN.equals(userRole);
		boolean isBuyer = transaction.getBuyerUser().getUserId().equals(requestingUserId);
		boolean isSeller = transaction.getSellerUser().getUserId().equals(requestingUserId);

		if (!isAdmin && !isBuyer && !isSeller) {
			throw new AccessDeniedException("您無權查看此交易的詳細資訊");
		}
		return transactionMapper.toDto(transaction);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TransactionDto> getTransactionsByBuyer(Integer buyserId) {
		return transactionRepository.findByBuyerUserUserIdOrderByCreatedAtDesc(buyserId).stream()
				.map(transactionMapper::toDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<TransactionDto> getTransactionsBySeller(Integer sellerId) {
		return transactionRepository.findBySellerUserUserIdOrderByCreatedAtDesc(sellerId).stream()
				.map(transactionMapper::toDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<TransactionDto> getAllTransactionsForAdmin() {
		return transactionRepository.findAll().stream().map(transactionMapper::toDto).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public TransactionDto updateTransactionsStatus(Integer transactionId, TransactionStatus newStatus,
			Integer requestingUserId, UserRole userRole) {
		Transaction transaction = transactionRepository.findById(transactionId)
				.orElseThrow(() -> new ProductNotFoundException("找不到交易 ID: " + transactionId));
		boolean isAdmin = UserRole.ADMIN.equals(userRole);
		boolean isSeller = transaction.getSellerUser().getUserId().equals(requestingUserId);

		if (!isAdmin && !isSeller) {
			throw new AccessDeniedException("您無權更新此交易的狀態");
		}

		transaction.setStatus(newStatus);
		Transaction upTransaction = transactionRepository.save(transaction);

		return transactionMapper.toDto(upTransaction);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TransactionDto> getTransactionsByBuyerAndStatuses(Integer buyerId, List<TransactionStatus> statuses)
			throws UserNotFoundException {
		if (!userRepository.existsById(buyerId)) {
			throw new UserNotFoundException("找不到買家 ID: " + buyerId);
		}
		return transactionRepository.findByBuyerUserUserIdAndStatusInOrderByCreatedAtDesc(buyerId, statuses).stream()
				.map(transactionMapper::toDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<TransactionDto> getTransactionsBySellerAndStatuses(Integer sellerId, List<TransactionStatus> statuses)
			throws UserNotFoundException {
		if (!userRepository.existsById(sellerId)) {
			throw new UserNotFoundException("找不到賣家 ID: " + sellerId);
		}
		return transactionRepository.findBySellerUserUserIdAndStatusInOrderByCreatedAtDesc(sellerId, statuses).stream()
				.map(transactionMapper::toDto).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public TransactionDto buyerCancelTransaction(Integer transactionId, Integer buyerUserId)
			throws UserNotFoundException {
		Transaction transaction = transactionRepository.findByTransactionIdAndBuyerUserUserId(transactionId, buyerUserId)
				.orElseThrow(() -> new ProductNotFoundException("找不到屬於您的交易 ID: " + transactionId + " 或您無權操作"));
		if (transaction.getStatus() != TransactionStatus.Pending_Payment
				&& transaction.getStatus() != TransactionStatus.Paid) {
			throw new ProductOperationException("訂單狀態為 " + transaction.getStatus() + "，無法取消");
		}
		transaction.setStatus(TransactionStatus.Cancelled);
    // 恢復產品庫存
		Product product = transaction.getProductId();
		product.setQuantity(product.getQuantity()+1); // 假設一次交易一個單位
		// 如果產品之前因為庫存為0而被標記為 Sold，現在可以改回 For_Sale (如果業務允許)
		//if(product.getStatus()== ProductStatus.Sold && product.getQuantity()>0) {
		//  product.setStatus(ProductStatus.For_Sale);	
		//}
		productRepository.save(product);
		Transaction cancelledTransaction = transactionRepository.save(transaction);
		return transactionMapper.toDto(cancelledTransaction);
	
	}

	@Override
	public List<TransactionDto> createTransactionsFromCart(Integer userId, CheckoutRequestDto checkoutRequest) throws UserNotFoundException {
		 List<CheckoutItemDto> checkoutItems = checkoutRequest.getItems();
		 
		 if (checkoutItems.isEmpty()) {
	            throw new ProductOperationException("結帳項目不能為空，無法建立交易");
	        }
		 // 檢查是否需要運送資訊
		 boolean requiresShipping = checkoutItems.stream()
	                .anyMatch(item -> item.getChosenTransactionMethodId().equals(TransactionMethod.ID_SHIPPING));
		 
		 if (requiresShipping && checkoutRequest.getShippingInfo() == null) {
	            throw new IllegalArgumentException("選擇物流運送時，必須提供運送資訊");
	        }
		 
		 User buyer = userRepository.findById(userId)
	                .orElseThrow(() -> new UserNotFoundException("找不到使用者 ID: " + userId));
		 
		 List<Transaction> newTransactions = new ArrayList<>();
		 
		 
		 
		 for (CheckoutItemDto checkoutItem : checkoutItems) {
	            CartItem cartItem = cartItemRepository.findByUser_UserIdAndProduct_ProductId(userId, checkoutItem.getProductId())
	                    .orElseThrow(() -> new ProductOperationException("非法請求：購物車中找不到商品 ID: " + checkoutItem.getProductId()));
	            
	            Product product = cartItem.getProduct();
	            int quantityToBuy = cartItem.getQuantity();

	            if (product.getQuantity() < quantityToBuy) {
	                throw new ProductOperationException("商品 '" + product.getProductContent().getTitle() + "' 庫存不足，訂單已取消");
	            }
	            
	            product.setQuantity(product.getQuantity() - quantityToBuy);
	            if (product.getQuantity() == 0) {
	                product.setStatus(ProductStatus.Sold);
	            }
	            productRepository.save(product);

	            Transaction newTransaction = new Transaction();
	            newTransaction.setBuyerUser(buyer);
	            newTransaction.setSellerUser(product.getSellerUser());
	            newTransaction.setProductId(product);
	            newTransaction.setFinalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantityToBuy)));
	            newTransaction.setStatus(TransactionStatus.Paid);

	            TransactionShipmentDetail shipmentDetail = new TransactionShipmentDetail();
	            
	            TransactionMethod chosenMethod = transactionMethodRepository.findById(checkoutItem.getChosenTransactionMethodId())
	                    .orElseThrow(() -> new ProductOperationException("無效的交易方式 ID: " + checkoutItem.getChosenTransactionMethodId()));
	            shipmentDetail.setMethodName(chosenMethod.getName());

	            if (chosenMethod.getMethodId().equals(TransactionMethod.ID_SHIPPING)) {
	                ShippingInfoDto shippingInfo = checkoutRequest.getShippingInfo();
	                shipmentDetail.setAddress(shippingInfo.getAddress());
	            }

	            newTransaction.setShipmentDetail(shipmentDetail);
	            shipmentDetail.setTransaction(newTransaction);
	            
	            newTransactions.add(transactionRepository.save(newTransaction));
	        }

	        List<Integer> productIdsToRemove = checkoutItems.stream().map(CheckoutItemDto::getProductId).collect(Collectors.toList());
	        List<CartItem> itemsToRemove = cartItemRepository.findByUser_UserIdAndProduct_ProductIdIn(userId, productIdsToRemove);
	        cartItemRepository.deleteAll(itemsToRemove);

	        return newTransactions.stream()
	                .map(transactionMapper::toDto)
	                .collect(Collectors.toList());
	}

	

}
