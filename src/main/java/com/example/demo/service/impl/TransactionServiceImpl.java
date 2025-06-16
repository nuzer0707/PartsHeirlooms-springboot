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
		Transaction transaction = transactionRepository
				.findByTransactionIdAndBuyerUserUserId(transactionId, buyerUserId)
				.orElseThrow(() -> new ProductNotFoundException("找不到屬於您的交易 ID: " + transactionId + " 或您無權操作"));
	// 檢查是否處於可取消的狀態 (例如，已付款但尚未出貨)
		if (transaction.getStatus() != TransactionStatus.Pending_Payment
				&& transaction.getStatus() != TransactionStatus.Paid) {
			throw new ProductOperationException("訂單狀態為 " + transaction.getStatus() + "，無法取消");
		}
	// 1. 更新交易狀態為「已取消」
		transaction.setStatus(TransactionStatus.Cancelled);
		// 恢復產品庫存
		Product product = transaction.getProductId();
		product.setQuantity(product.getQuantity() + 1); // 假設一次交易一個單位
    // 如果商品之前因為這筆訂單而被標記為 Sold，現在應該將其恢復為 For_Sale
    // 這樣商品才能被重新上架銷售
    if (product.getStatus() == ProductStatus.Sold) {
        product.setStatus(ProductStatus.For_Sale);
    }
		// 如果產品之前因為庫存為0而被標記為 Sold，現在可以改回 For_Sale (如果業務允許)
		// if(product.getStatus()== ProductStatus.Sold && product.getQuantity()>0) {
		// product.setStatus(ProductStatus.For_Sale);
		// }
		productRepository.save(product);
		Transaction cancelledTransaction = transactionRepository.save(transaction);
		return transactionMapper.toDto(cancelledTransaction);

	}

	@Override
	@Transactional
	public List<TransactionDto> createTransactionsFromCart(Integer userId, CheckoutRequestDto checkoutRequest)
			throws UserNotFoundException {
		List<CheckoutItemDto> checkoutItems = checkoutRequest.getItems();
        if (checkoutItems == null || checkoutItems.isEmpty()) {
            throw new ProductOperationException("結帳項目不能為空，無法建立交易");
        }

        // 1. 前置檢查：檢查是否需要運送資訊，如果需要但未提供，則提前拋出例外
        boolean requiresShipping = checkoutItems.stream()
                .anyMatch(item -> TransactionMethod.ID_SHIPPING.equals(item.getChosenTransactionMethodId()));
        if (requiresShipping && checkoutRequest.getShippingInfo() == null) {
            throw new IllegalArgumentException("選擇物流運送時，必須提供運送資訊");
        }

        User buyer = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("找不到使用者 ID: " + userId));

        List<Transaction> createdTransactions = new ArrayList<>();

        // 2. 遍歷每一個要結帳的商品
        for (CheckoutItemDto checkoutItem : checkoutItems) {
            
            // 從購物車中找到對應的項目，確保請求合法性並獲取商品實體
            CartItem cartItem = cartItemRepository.findByUser_UserIdAndProduct_ProductId(userId, checkoutItem.getProductId())
                    .orElseThrow(() -> new ProductOperationException("非法請求：購物車中找不到商品 ID: " + checkoutItem.getProductId()));

            Product product = cartItem.getProduct();
            int quantityToBuy = cartItem.getQuantity();

            // 3. 檢查庫存並更新
            if (product.getQuantity() < quantityToBuy) {
                // 如果任何一個商品庫存不足，整個交易失敗（所有資料庫操作都會回滾）
                throw new ProductOperationException("商品 '" + product.getProductContent().getTitle() + "' 庫存不足，訂單已取消");
            }
            product.setQuantity(product.getQuantity() - quantityToBuy);
            if (product.getQuantity() == 0) {
                product.setStatus(ProductStatus.Sold);
            }
            // 此處的 save 會由 JPA 的 Dirty Checking 機制在交易結束時自動處理，也可以手動 save
            productRepository.save(product);


            // 4. 根據買家選擇，找到對應的 ProductTransactionDetail
            ProductTransactionDetail chosenDetail = product.getTransactionDetails().stream()
                    .filter(detail -> detail.getTransactionMethod().getMethodId().equals(checkoutItem.getChosenTransactionMethodId()))
                    .findFirst()
                    .orElseThrow(() -> new ProductOperationException("商品 '" + product.getProductContent().getTitle() 
                                    + "' 不支援所選的交易方式 ID: " + checkoutItem.getChosenTransactionMethodId()));

            
            // 5. 建立 Transaction 主體
            Transaction newTransaction = new Transaction();
            newTransaction.setBuyerUser(buyer);
            newTransaction.setSellerUser(product.getSellerUser());
            newTransaction.setProductId(product);
            newTransaction.setFinalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantityToBuy)));
            newTransaction.setChosenTransactionDetail(chosenDetail);
            newTransaction.setStatus(TransactionStatus.Paid); // 假設結帳後即為「已付款」狀態

            // 6. 建立 TransactionShipmentDetail (交易運送資訊快照)
            TransactionShipmentDetail shipmentDetail = createShipmentDetail(chosenDetail, checkoutRequest.getShippingInfo());
            newTransaction.setShipmentDetail(shipmentDetail);
            shipmentDetail.setTransaction(newTransaction); // 建立雙向關聯

            // 7. 儲存交易，JPA 會一併儲存關聯的 shipmentDetail
            createdTransactions.add(transactionRepository.save(newTransaction));
        }

        // 8. 從購物車中移除已結帳的商品
        List<Integer> productIdsToRemove = checkoutItems.stream()
                .map(CheckoutItemDto::getProductId)
                .collect(Collectors.toList());
        cartItemRepository.deleteByUser_UserIdAndProduct_ProductIdIn(userId, productIdsToRemove); // 假設有這個 Repository 方法

        // 9. 將所有成功建立的 Transaction 轉換為 DTO 並返回
        return createdTransactions.stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 輔助方法：根據賣家設定的交易細節和買家填寫的運送資訊，建立一個交易運送快照。
     * @param chosenDetail 賣家設定的、被買家選中的交易方式細節
     * @param shippingInfo 買家填寫的運送資訊 (可能為 null)
     * @return 一個新的 TransactionShipmentDetail 物件
     */
    private TransactionShipmentDetail createShipmentDetail(ProductTransactionDetail chosenDetail, ShippingInfoDto shippingInfo) {
        TransactionShipmentDetail shipmentDetail = new TransactionShipmentDetail();
        TransactionMethod chosenMethod = chosenDetail.getTransactionMethod();
        
        shipmentDetail.setMethodName(chosenMethod.getName());
        shipmentDetail.setNotes(chosenDetail.getGeneralNotes()); 

        if (TransactionMethod.ID_SHIPPING.equals(chosenMethod.getMethodId())) {
            // 處理【物流】的情況
            // 在主方法中已檢查過 shippingInfo 在此情況下不為 null
            shipmentDetail.setAddress(shippingInfo.getAddress());
            shipmentDetail.setNotes(chosenDetail.getGeneralNotes()); // 複製賣家的備註
            
            
        } else if (TransactionMethod.ID_MEETUP.equals(chosenMethod.getMethodId())) {
            // 處理【面交】的情況
            shipmentDetail.setMeetupTime(chosenDetail.getMeetupTime());
            shipmentDetail.setNotes(chosenDetail.getGeneralNotes()); // 複製賣家的備註
            shipmentDetail.setMeetupLatitude(chosenDetail.getMeetupLatitude());
            shipmentDetail.setMeetupLongitude(chosenDetail.getMeetupLongitude());
            
            // 面交的 address 欄位保持 null
        }
        
        return shipmentDetail;
	}

}
