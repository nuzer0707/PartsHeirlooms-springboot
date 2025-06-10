package com.example.demo.controller;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.CategoryNotFoundException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.ProductOperationException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.SellerVerificationDto;
import com.example.demo.model.dto.TransactionDto;
import com.example.demo.model.dto.products.ProductAddDto;
import com.example.demo.model.dto.products.ProductDto;
import com.example.demo.model.dto.products.ProductSummaryDto;
import com.example.demo.model.dto.products.ProductUpdateDto;
import com.example.demo.model.dto.users.UserCert;
import com.example.demo.model.entity.enums.ProductStatus;
import com.example.demo.model.entity.enums.TransactionStatus;
import com.example.demo.model.entity.enums.UserRole;
import com.example.demo.model.entity.enums.VerificationStatus;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.ProductService;
import com.example.demo.service.SellerVerificationService;
import com.example.demo.service.TransactionService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = { "/seller" })
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:8002" }, allowCredentials = "true")
public class SellerController {

	private static final Logger logger = LoggerFactory.getLogger(SellerController.class);

	@Autowired
	private ProductService productService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private SellerVerificationService  sellerVerificationService;
	
	

  private UserCert checkAndGetVerifiedSeller(HttpSession session) throws AccessDeniedException {
    UserCert userCert = (UserCert) session.getAttribute("userCert");

    // Filter 應該已經確保了 userCert 不為 null 且角色是 SELLER 或 ADMIN
    if (userCert == null) {
        // 理論上不應發生，如果 Filter 正常工作
        logger.warn("UserCert is null in SellerController. SellerApiAuthFilter might not be working as expected.");
        throw new AccessDeniedException("您尚未登入或登入狀態已逾期，請重新登入。");
    }

    // 如果是 ADMIN，則不需要檢查賣家資格
    if (userCert.getPrimaryRole() == UserRole.ADMIN) {
        return userCert;
    }

    // 如果是 SELLER，則必須檢查其資格審核狀態
    if (userCert.getPrimaryRole() == UserRole.SELLER) {
        try {
            SellerVerificationDto verificationStatusDto = sellerVerificationService.getUserVerificationStatus(userCert.getUserId());
            if (verificationStatusDto == null || verificationStatusDto.getStatus() != VerificationStatus.Approved) {
                throw new AccessDeniedException("您的賣家資格尚未通過審核或未申請，無法執行此操作。");
            }
            return userCert; // 資格通過
        } catch (ProductNotFoundException e) { // 假設 getUserVerificationStatus 在未找到時拋出此異常
            logger.info("Seller verification status not found for user ID: {}, access denied.", userCert.getUserId());
            throw new AccessDeniedException("您尚未提交賣家申請，無法執行此操作。");
        } catch (UserNotFoundException e) { // 理論上不應發生，因為 userCert 存在
             logger.error("User not found during seller verification check for user ID: {}, which should exist.", userCert.getUserId(), e);
             throw new AccessDeniedException("驗證賣家資格時發生用戶查找錯誤。");
        }
        // 不需要捕獲 AccessDeniedException，因為我們在這裡就是主動拋出它
    }

    // 如果角色不是 SELLER 也不是 ADMIN (理論上 Filter 應該攔截，但作為防禦)
    logger.warn("User with role {} (ID: {}) reached seller-specific logic. Filter might have an issue.", userCert.getPrimaryRole(), userCert.getUserId());
    throw new AccessDeniedException("權限不足，無法執行此操作。");
}


	
	// ==========================================================
	// 刊登/管理商品
	// ==========================================================

	@GetMapping("/products")
	public ResponseEntity<ApiResponse<List<ProductSummaryDto>>> getMyProductsAsSeller(
		@RequestParam(required = false) ProductStatus status, HttpSession session) {
		
		try {
			UserCert userCert = checkAndGetVerifiedSeller(session); 
			
			List<ProductSummaryDto> products;
			if (status != null) {
				products = productService.getProductsBySellerAndStatus(userCert.getUserId(), status);
			} else {
				products = productService.getAllProductsBySeller(userCert.getUserId());// 獲取該賣家的所有商品

			}
			if (products.isEmpty()) {
				return ResponseEntity.ok(ApiResponse.success("您尚未刊登任何商品或無符合條件的商品", products));
			}
			return ResponseEntity.ok(ApiResponse.success("查詢您刊登的商品成功", products));
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系統錯誤：" + e.getMessage()));
		}
	}

	@PostMapping("/products")
	public ResponseEntity<ApiResponse<ProductDto>> addProductBySeller(@Valid @RequestBody ProductAddDto addDto,
			HttpSession session) {

	

		try {
			UserCert userCert = checkAndGetVerifiedSeller(session); 
			
			ProductDto newProduct = productService.addProduct(addDto, userCert.getUserId());
			return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("商品刊登成功", newProduct));

		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系統錯誤：" + e.getMessage()));
		} catch (CategoryNotFoundException|ProductOperationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "刊登失敗：" + e.getMessage()));
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "刊登失敗：" + e.getMessage()));
		}

	}

	@GetMapping("/products/{productId}")
	public ResponseEntity<ApiResponse<ProductDto>> getMyProductDetailsAsSeller(@PathVariable Integer productId,
			HttpSession session) {

	
		 
		try {
			UserCert userCert = checkAndGetVerifiedSeller(session); 
			ProductDto product = productService.getProductByIdForOwnerOrAdmin(productId, userCert.getUserId(),
					userCert.getPrimaryRole());
			return ResponseEntity.ok(ApiResponse.success("查詢商品詳情成功", product));
		} catch (ProductNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
		}
	}

	@PutMapping("/products/{productId}")
	public ResponseEntity<ApiResponse<ProductDto>> updateProductBySeller(@PathVariable Integer productId,
			@Valid @RequestBody ProductUpdateDto updateDto, HttpSession session) {
	
		try {
			UserCert userCert = checkAndGetVerifiedSeller(session); 
			ProductDto updateProductDto = productService.updateProduct(productId, updateDto, userCert.getUserId(),
					userCert.getPrimaryRole());
			return ResponseEntity.ok(ApiResponse.success("商品更新成功", updateProductDto));
		} catch (ProductNotFoundException | CategoryNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "更新失敗：" + e.getMessage()));
		} catch (ProductOperationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "更新失敗：" + e.getMessage()));
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "更新失敗：" + e.getMessage()));
		}
	}

	@PutMapping("/products/{productId}/status")
	public ResponseEntity<ApiResponse<ProductDto>> updateMyProductStatus(@PathVariable Integer productId,
			@RequestParam ProductStatus newStatus, HttpSession session) {
		
		try {
			UserCert userCert = checkAndGetVerifiedSeller(session); 
			ProductDto updatrProduct = productService.updateProductStatus(productId, newStatus, userCert.getUserId(),
					userCert.getPrimaryRole());
			return ResponseEntity.ok(ApiResponse.success("產品狀態更新成功", updatrProduct));
		} catch (ProductNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
		} catch (ProductOperationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
		}
	}

	@DeleteMapping("/products/{productId}")
	public ResponseEntity<ApiResponse<Void>> deleteProductBySeller(@PathVariable Integer productId, HttpSession session) {
	
		try {
			UserCert userCert = checkAndGetVerifiedSeller(session); 
			productService.deleteProduct(productId, userCert.getUserId(), userCert.getPrimaryRole());
			return ResponseEntity.ok(ApiResponse.success("商品 " + productId + " 刪除成功", null));
		} catch (ProductNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "刪除失敗：" + e.getMessage()));
		} catch (ProductOperationException e) { // 例如，商品有關聯訂單不能刪除
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "刪除失敗：" + e.getMessage()));
		}
		// 【修改】將 AccessDeniedException 移到 ProductOperationException 之前或合併，因為 Service
		// 層可能先拋出 AccessDenied
		catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "刪除失敗：" + e.getMessage()));
		}

	}

	// ==========================================================
	// 賣家 - 交易訂單
	// ==========================================================

	@GetMapping("/orders/received/current")
	public ResponseEntity<ApiResponse<List<TransactionDto>>> getSellerCurrentOrders(HttpSession session) throws UserNotFoundException {
		
		try {
      UserCert userCert = checkAndGetVerifiedSeller(session);
      List<TransactionStatus> currentStatuses = Arrays.asList(
              // 【思考】賣家可能也需要看到 Pending_Payment 的訂單以作準備
              // TransactionStatus.Pending_Payment,
              TransactionStatus.Paid,
              TransactionStatus.Processing,
              TransactionStatus.Shipped // Shipped 也可以認為是當前未完成的
      );
      List<TransactionDto> transactionDtos = transactionService.getTransactionsBySellerAndStatuses(userCert.getUserId(), currentStatuses);
      if (transactionDtos.isEmpty()) {
          return ResponseEntity.ok(ApiResponse.success("目前沒有待處理的銷售訂單", transactionDtos));
      }
      return ResponseEntity.ok(ApiResponse.success("查詢當前銷售訂單成功", transactionDtos));
  } catch (AccessDeniedException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
  }
 
}


	@GetMapping("/orders/received/{transactionId}")
	public ResponseEntity<ApiResponse<TransactionDto>> getSellerTransactionDetails(@PathVariable Integer transactionId,
			HttpSession session) {
	
		try {
		  UserCert userCert = checkAndGetVerifiedSeller(session);
			TransactionDto transactionDto = transactionService.getTransactionById(transactionId, userCert.getUserId(),
					userCert.getPrimaryRole());
			return ResponseEntity.ok(ApiResponse.success("查詢訂單詳情成功", transactionDto));
		} catch (ProductNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
		}

	}

	@PutMapping("/orders/received/{transactionId}/status")
	public ResponseEntity<ApiResponse<TransactionDto>> updateSellerOrderStatus(@PathVariable Integer transactionId,
			@RequestParam TransactionStatus newStatus, HttpSession session) {
		try {
		  UserCert userCert = checkAndGetVerifiedSeller(session);
			TransactionDto transactionDto = transactionService.updateTransactionsStatus(transactionId, newStatus,
					userCert.getUserId(), userCert.getPrimaryRole());
			return ResponseEntity.ok(ApiResponse.success("訂單狀態更新成功", transactionDto));
		} catch (ProductNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
		} catch (ProductOperationException e) { // 例如 "狀態不允許更新"
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
		}

	}

	// ==========================================================
	// 賣家 - 交易歷史
	// ==========================================================

	@GetMapping("/orders/received/history")
	public ResponseEntity<ApiResponse<List<TransactionDto>>> getSellerOrderHistory(HttpSession session)
			throws UserNotFoundException {

		try {
      UserCert userCert = checkAndGetVerifiedSeller(session);
      List<TransactionStatus> historyStatus = Arrays.asList(
              TransactionStatus.Completed, // 已完成
              TransactionStatus.Cancelled  // 已取消
              // Shipped 狀態如果算在 "當前訂單" 中，則這裡不應包含，反之亦然，保持定義一致
      );
      List<TransactionDto> transactionDtos = transactionService.getTransactionsBySellerAndStatuses(userCert.getUserId(), historyStatus);
      if (transactionDtos.isEmpty()) {
          return ResponseEntity.ok(ApiResponse.success("沒有歷史銷售訂單紀錄", transactionDtos));
      }
      return ResponseEntity.ok(ApiResponse.success("查詢歷史銷售訂單成功", transactionDtos));
  } catch (AccessDeniedException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
  }
  // 【移除】catch (UserNotFoundException e)
}


	private ResponseEntity<ApiResponse<Void>> unauthorizedResponse(String message) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), message));
	}

	private <T> ResponseEntity<ApiResponse<T>> unauthorizedGenericResponse(String message) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), message));
	}

}
